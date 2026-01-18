package com.incomingcallonly.launcher.util

import android.content.Context
import android.telephony.TelephonyManager

object PhoneNumberUtils {
    
    /**
     * Normalizes a phone number to international format with country code.
     * @param phoneNumber The raw phone number to normalize
     * @param context Android context to detect device's country
     * @param defaultCountryCode Fallback country code if detection fails (e.g., "+33")
     * @return The normalized phone number in international format (e.g., "+33612345678")
     */
    fun normalizePhoneNumber(
        phoneNumber: String,
        context: Context,
        defaultCountryCode: String = "+33"
    ): String {
        val cleanedNumber = phoneNumber
            .replace(" ", "")
            .replace("-", "")
            .replace("(", "")
            .replace(")", "")
            .replace(".", "")
        
        // If already in international format, just return cleaned
        if (cleanedNumber.startsWith("+")) {
            return cleanedNumber
        }
        
        // Handle numbers starting with 00 (international prefix)
        if (cleanedNumber.startsWith("00")) {
            return "+" + cleanedNumber.drop(2)
        }
        
        // Get device's country code
        val deviceCountryCode = getDeviceCountryCode(context) ?: defaultCountryCode
        
        // If the number already starts with the country code (e.g. "336..." for FR), 
        // treat it as international format
        val deviceCodeDigits = deviceCountryCode.removePrefix("+")
        if (cleanedNumber.startsWith(deviceCodeDigits)) {
            return "+" + cleanedNumber
        }

        // Handle local numbers starting with 0
        return if (cleanedNumber.startsWith("0")) {
            deviceCountryCode + cleanedNumber.drop(1)
        } else {
            deviceCountryCode + cleanedNumber
        }
    }
    
    /**
     * Gets the device's country dial code based on network or SIM.
     */
    private fun getDeviceCountryCode(context: Context): String? {
        return try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            val countryIso = tm?.networkCountryIso?.uppercase() 
                ?: tm?.simCountryIso?.uppercase()
            countryIso?.let { getCountryCodeFromIso(it) }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Gets the dial code from a country ISO (e.g., "FR" -> "+33")
     */
    private fun getCountryCodeFromIso(countryIso: String): String? {
        return when (countryIso.uppercase()) {
            "FR" -> "+33"
            "BE" -> "+32"
            "CH" -> "+41"
            "US", "CA" -> "+1"
            "GB" -> "+44"
            "DE" -> "+49"
            "ES" -> "+34"
            "IT" -> "+39"
            "PT" -> "+351"
            "LU" -> "+352"
            "NL" -> "+31"
            "AT" -> "+43"
            "PL" -> "+48"
            "CZ" -> "+420"
            "GR" -> "+30"
            "SE" -> "+46"
            "NO" -> "+47"
            "DK" -> "+45"
            "FI" -> "+358"
            "IE" -> "+353"
            "RO" -> "+40"
            "HU" -> "+36"
            "SK" -> "+421"
            "HR" -> "+385"
            "SI" -> "+386"
            "BG" -> "+359"
            "LT" -> "+370"
            "LV" -> "+371"
            "EE" -> "+372"
            "MT" -> "+356"
            "CY" -> "+357"
            "MA" -> "+212"
            "TN" -> "+216"
            "DZ" -> "+213"
            "SN" -> "+221"
            "CI" -> "+225"
            "CM" -> "+237"
            "MG" -> "+261"
            "MU" -> "+230"
            "RE" -> "+262"
            "GP" -> "+590"
            "MQ" -> "+596"
            "GF" -> "+594"
            "NC" -> "+687"
            "PF" -> "+689"
            else -> null
        }
    }
    
    /**
     * Compares two phone numbers to check if they are equivalent.
     * Handles different formats (local vs international).
     */
    fun arePhoneNumbersEqual(
        number1: String,
        number2: String,
        context: Context,
        defaultCountryCode: String = "+33"
    ): Boolean {
        val normalized1 = normalizePhoneNumber(number1, context, defaultCountryCode)
        val normalized2 = normalizePhoneNumber(number2, context, defaultCountryCode)
        return normalized1 == normalized2
    }
}
