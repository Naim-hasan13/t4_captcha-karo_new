package com.captchakaro.appy

import android.content.Context

object BalanceManager {
    private const val PREFS_NAME = "UserPrefs"
    private const val BALANCE_KEY = "balanceKey"
    private const val DAILY_EARNINGS_KEY = "dailyEarningsKey"
    private const val LAST_UPDATED_DATE_KEY = "lastUpdatedDateKey"

    /**
     * Get the current balance from SharedPreferences.
     *
     * @param context the context used to access SharedPreferences.
     * @return the current balance.
     */
    fun getBalance(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(BALANCE_KEY, 0)
    }

    /**
     * Update the balance by adding points to the current balance.
     * This ensures that the balance is always cumulative and persists.
     *
     * @param context the context used to access SharedPreferences.
     * @param points the points to add to the balance.
     */
    fun addPoints(context: Context, points: Int) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentBalance = getBalance(context)
        val newBalance = currentBalance + points
        saveBalance(context, newBalance)

        // Update daily earnings
        val currentEarnings = getDailyEarnings(context)
        saveDailyEarnings(context, currentEarnings + points)
    }

    /**
     * Deduct points from the balance (e.g., during a withdrawal).
     *
     * @param context the context used to access SharedPreferences.
     * @param points the points to deduct from the balance.
     */
    fun withdrawPoints(context: Context, points: Int): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentBalance = getBalance(context)

        return if (currentBalance >= points) {
            val newBalance = currentBalance - points
            saveBalance(context, newBalance)
            true
        } else {
            false // Not enough balance
        }
    }

    /**
     * Get today's earnings. Reset the earnings if the date has changed.
     *
     * @param context the context used to access SharedPreferences.
     * @return the daily earnings for today.
     */
    fun getDailyEarnings(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        resetEarningsIfDateChanged(context)
        return sharedPreferences.getInt(DAILY_EARNINGS_KEY, 0)
    }

    /**
     * Reset the daily earnings if the date has changed.
     *
     * @param context the context used to access SharedPreferences.
     */
    private fun resetEarningsIfDateChanged(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastUpdatedDate = sharedPreferences.getString(LAST_UPDATED_DATE_KEY, null)
        val currentDate = getCurrentDate()

        if (lastUpdatedDate != currentDate) {
            // Date has changed, reset daily earnings
            saveDailyEarnings(context, 0)

            // Update the date
            sharedPreferences.edit().putString(LAST_UPDATED_DATE_KEY, currentDate).apply()
        }
    }

    /**
     * Save the balance persistently in SharedPreferences.
     *
     * @param context the context used to access SharedPreferences.
     * @param balance the new balance to save.
     */
    private fun saveBalance(context: Context, balance: Int) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt(BALANCE_KEY, balance).apply()
    }

    /**
     * Save the daily earnings persistently in SharedPreferences.
     *
     * @param context the context used to access SharedPreferences.
     * @param earnings the new daily earnings to save.
     */
    private fun saveDailyEarnings(context: Context, earnings: Int) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt(DAILY_EARNINGS_KEY, earnings).apply()
    }

    /**
     * Get the current date in a simple format (e.g., "yyyy-MM-dd").
     *
     * @return the current date as a String.
     */
    private fun getCurrentDate(): String {
        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return formatter.format(java.util.Date())
    }
}
