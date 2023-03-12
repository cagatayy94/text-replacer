
# Auto "messages.properties" file generator for any language

Automatically creates "messages.properties" file for your multilingual projects.

All you have to do is to put the original "messages.properties" file and the translations.xlsx file containing the translations under the "src/main/resources/original" directory.

After running the program, the translated file with the suffix you specified is in the same directory:
"messages_[TARGET_LANGUAGE].properties"
will be created.

## Translation.XLSX

#### This excel file sould contains 2 columns as follows:

```http
  file: src/main/resources/original/translations.xlsx
```

| MainLanguage | TargetLanguage     |
| :-------- | :------- |
| `Depresyon`      | `Dépression` |
| `Kardiyovasküler hastalıklar`      | `Maladies Cardiovasculaires` |
| `İnfeksiyonlar`      | `Infection` |
| `Polifarmasi (çok sayıda ilaç kullanma ve buna bağlı yan etkiler)`      | `Polypharmacie (utilisation d'un grand nombre de médicaments et effets secondaires qui y sont liés)` |
| `Çoklu kronik hastalıklar`      | `Maladies chroniques multiples` |
| `Kanser`      | `Cancer` |

Loop wiil get througt "messages.properties" file then replace all them from translations.xlsx file and finally save it as new name.