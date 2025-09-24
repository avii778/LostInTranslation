package translation;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;



// TODO Task D: Update the GUI for the program to align with UI shown in the README example.
//            Currently, the program only uses the CanadaTranslator and the user has
//            to manually enter the language code they want to use for the translation.
//            See the examples package for some code snippets that may be useful when updating
//            the GUI.
public class GUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Translator translator = new JSONTranslator();

            JPanel languagePanel = new JPanel();

            List<String> langs = translator.getLanguageCodes();
            String[] languageItems = langs.toArray(new String[0]);

            languagePanel.add(new JLabel("Language:"));

            // create combobox, add country codes into it, and add it to our panel
            JComboBox<String> languageComboBox = new JComboBox<>();
            LanguageCodeConverter langconverter = new LanguageCodeConverter();
            for(String languageCode : langs) {
                languageComboBox.addItem(langconverter.fromLanguageCode(languageCode));
            }
            languagePanel.add(languageComboBox);

            JList<String> languageList = new JList<>(languageItems);


            JLabel translationLabel = new JLabel("Translation: ");
            translationLabel.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
            translationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);


            List<String> alpha3Codes = translator.getCountryCodes();
            List<String> countryDisplayNames = new ArrayList<>(alpha3Codes.size());
            for (String c : alpha3Codes) {
                String englishName = translator.translate(c, "en");
                countryDisplayNames.add(englishName != null ? englishName : c);
            }
            String[] countryItems = countryDisplayNames.toArray(new String[0]);

            JList<String> countryList = new JList<>(countryItems);
            countryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            countryList.setVisibleRowCount(12);
            JScrollPane countryScroll = new JScrollPane(countryList);

            if (!alpha3Codes.isEmpty()) {
                countryList.setSelectedIndex(0);
            }

            ListSelectionListener updateTranslation = new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) return;
                    String displayName = languageComboBox.getSelectedItem().toString();

                    String langCode = langconverter.fromLanguage(displayName);

                    if (langs.contains(langCode)) {
                        languageList.setSelectedValue(langCode, true);
                    }

                    String lang = languageList.getSelectedValue();
                    int countryIdx = countryList.getSelectedIndex();
                    if (lang == null || countryIdx < 0) {
                        translationLabel.setText("Translation: ");
                        return;
                    }
                    String countryAlpha3 = alpha3Codes.get(countryIdx);
                    String translated = translator.translate(countryAlpha3, lang);
                    if (translated == null || translated.isBlank()) {
                        translated = "(no translation found)";
                    }
                    translationLabel.setText("Translation: " + translated);
                }
            };
            languageList.addListSelectionListener(updateTranslation);
            countryList.addListSelectionListener(updateTranslation);

            updateTranslation.valueChanged(new ListSelectionEvent(languageList, 0, 0, false));

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.add(languagePanel);
            mainPanel.add(translationLabel);
            mainPanel.add(countryScroll);

            JFrame frame = new JFrame("Country Name Translator");
            frame.setContentPane(mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.pack();
            frame.setVisible(true);
        });
    }
}
