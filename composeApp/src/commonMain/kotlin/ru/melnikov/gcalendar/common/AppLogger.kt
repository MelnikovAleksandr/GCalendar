package ru.melnikov.gcalendar.common

import co.touchlab.kermit.Logger

object AppLogger {
    private val logger = Logger.withTag("GCalendar")

    fun v(message: () -> String) {
        logger.v { message() }
    }

    fun v(throwable: Throwable, message: () -> String) {
        logger.v(throwable) { message() }
    }

    fun d(message: () -> String) {
        logger.d { message() }
    }

    fun d(throwable: Throwable, message: () -> String) {
        logger.d(throwable) { message() }
    }

    fun i(message: () -> String) {
        logger.i { message() }
    }

    fun i(throwable: Throwable, message: () -> String) {
        logger.i(throwable) { message() }
    }

    fun w(message: () -> String) {
        logger.w { message() }
    }

    fun w(throwable: Throwable, message: () -> String) {
        logger.w(throwable) { message() }
    }

    fun e(message: () -> String) {
        logger.e { message() }
    }

    fun e(throwable: Throwable, message: () -> String) {
        logger.e(throwable) { message() }
    }

    fun a(message: () -> String) {
        logger.a { message() }
    }

    fun a(throwable: Throwable, message: () -> String) {
        logger.a(throwable) { message() }
    }

    fun withTag(tag: String): Logger = Logger.withTag("GCalendar:$tag")
}