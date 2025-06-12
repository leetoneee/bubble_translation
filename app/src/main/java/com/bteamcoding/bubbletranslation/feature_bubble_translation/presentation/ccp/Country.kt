package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.ccp

enum class Country(val countryIso: String, val countryName: String, val countryCode: String) {
    Afrikaans("ZA", "Afrikaans", "af"),
    Arabic("SA", "Arabic", "ar"),
    Belarusian("BY", "Belarusian", "be"),
    Bulgarian("BG", "Bulgarian", "bg"),
    Bengali("BD", "Bengali", "bn"),
    Catalan("ES", "Catalan", "ca"),
    Czech("CZ", "Czech", "cs"),
    Danish("DK", "Danish", "da"),
    German("DE", "German", "de"),
    Greek("GR", "Greek", "el"),
    English("GB", "English", "en"),
    Spanish("ES", "Spanish", "es"),
    Estonian("EE", "Estonian", "et"),
    Persian("IR", "Persian", "fa"),
    Finnish("FI", "Finnish", "fi"),
    French("FR", "French", "fr"),
    Irish("IE", "Irish", "ga"),
    Galician("ES", "Galician", "gl"),
    Gujarati("IN", "Gujarati", "gu"),
    Hebrew("IL", "Hebrew", "he"),
    Hindi("IN", "Hindi", "hi"),
    Croatian("HR", "Croatian", "hr"),
    Haitian("HT", "Haitian", "ht"),
    Hungarian("HU", "Hungarian", "hu"),
    Indonesian("ID", "Indonesian", "id"),
    Icelandic("IS", "Icelandic", "is"),
    Italian("IT", "Italian", "it"),
    Japanese("JP", "Japanese", "ja"),
    Georgian("GE", "Georgian", "ka"),
    Kannada("IN", "Kannada", "kn"),
    Korean("KR", "Korean", "ko"),
    Lithuanian("LT", "Lithuanian", "lt"),
    Latvian("LV", "Latvian", "lv"),
    Macedonian("MK", "Macedonian", "mk"),
    Marathi("IN", "Marathi", "mr"),
    Malay("MY", "Malay", "ms"),
    Maltese("MT", "Maltese", "mt"),
    Dutch("NL", "Dutch", "nl"),
    Norwegian("NO", "Norwegian", "no"),
    Polish("PL", "Polish", "pl"),
    Portuguese("PT", "Portuguese", "pt"),
    Romanian("RO", "Romanian", "ro"),
    Russian("RU", "Russian", "ru"),
    Slovak("SK", "Slovak", "sk"),
    Slovenian("SI", "Slovenian", "sl"),
    Albanian("AL", "Albanian", "sq"),
    Swedish("SE", "Swedish", "sv"),
    Swahili("KE", "Swahili", "sw"),
    Tamil("LK", "Tamil", "ta"),
    Telugu("IN", "Telugu", "te"),
    Thai("TH", "Thai", "th"),
    Tagalog("PH", "Tagalog", "tl"),
    Turkish("TR", "Turkish", "tr"),
    Ukrainian("UA", "Ukrainian", "uk"),
    Urdu("PK", "Urdu", "ur"),
    Vietnamese("VN", "Vietnamese", "vi"),
    Chinese("CN", "Chinese", "zh");

    companion object {


        /**
         * Get all countries
         * @return List<Countries>
         */
        fun getAllCountries(): List<Country> {
            return entries.sortedBy { it.countryName }
        }

        /**
         * Get selected countries
         * @param selectedCountries List<Countries>
         * @return List<Countries>
         */
        fun getSelectedCountries(selectedCountries: List<Country>): List<Country> {
            return selectedCountries
        }

        /**
         * Get all countries except selected countries
         * @param selectedCountries List<Countries>
         * @return List<Countries>
         */
        fun getAllCountriesExcept(selectedCountries: List<Country>): List<Country> {
            return entries.filter { it !in selectedCountries }
        }

        /**
         * Get country by iso
         * @param iso String
         * @return Country?
         */
        fun getCountryByIso(iso: String): Country? {
            return entries.find { it.countryIso == iso.uppercase() }
        }


        /**
         * Search country by query
         * @param query String
         * @param list List<Countries>
         * @return List<Countries>
         */
        fun searchCountry(query: String, list: List<Country>): List<Country> {
            val normalizedQuery = query.trim()
            return list.filter { country ->
                country.countryIso.contains(normalizedQuery, true) || country.countryName.contains(
                    normalizedQuery,
                    true
                ) || country.countryCode.contains(normalizedQuery, true)
            }
        }


    }

}