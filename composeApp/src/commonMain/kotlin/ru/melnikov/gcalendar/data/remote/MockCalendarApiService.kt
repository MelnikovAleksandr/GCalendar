package ru.melnikov.gcalendar.data.remote

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.koin.core.annotation.Single
import ru.melnikov.gcalendar.domain.model.Calendar
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.Holiday
import kotlin.random.Random
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

@Single
class MockCalendarApiService : CalendarApiService {

    private val calendarTypes = mapOf(
        "work" to "Work",
        "personal" to "Personal",
        "family" to "Family",
        "fitness" to "Fitness",
        "travel" to "Travel",
        "study" to "Study",
        "finance" to "Finance",
        "health" to "Health"
    )

    private val eventTitles = mapOf(
        "work" to listOf(
            "Team Meeting", "Sprint Planning", "Product Demo", "Client Call",
            "Design Review", "Performance Review", "1:1 with Manager", "Department Lunch",
            "Project Kickoff", "Code Review", "Release Planning", "Interview",
            "Training Session", "Conference Call", "Board Meeting"
        ),
        "personal" to listOf(
            "Dinner with Friends", "Movie Night", "Shopping", "Home Maintenance",
            "Call Parents", "Haircut", "Pay Bills", "Read Book", "Writing Session",
            "Gardening", "Cooking Class", "Visit Friends", "Clean Apartment"
        ),
        "family" to listOf(
            "Family Dinner", "School Meeting", "Soccer Practice", "Dance Recital",
            "Parent Teacher Conference", "Family Movie Night", "Weekend Trip",
            "Birthday Party", "Anniversary Celebration"
        ),
        "fitness" to listOf(
            "Morning Run", "Gym Workout", "Yoga Class", "Swimming", "Cycling",
            "Hiking Trip", "Sports Game", "Meditation", "Personal Trainer Session"
        ),
        "travel" to listOf(
            "Flight to Paris", "Hotel Check-in", "Sightseeing Tour", "Car Rental",
            "Museum Visit", "Restaurant Reservation", "Beach Day", "Hiking Trip"
        ),
        "study" to listOf(
            "Study Session", "Exam Prep", "Group Project", "Research", "Writing",
            "Assignment Due", "Online Course", "Reading Session", "Lab Work"
        ),
        "finance" to listOf(
            "Budget Review", "Tax Preparation", "Investment Check", "Mortgage Payment",
            "Financial Advisor Meeting", "Expense Report", "Account Reconciliation"
        ),
        "health" to listOf(
            "Doctor Appointment", "Dentist Appointment", "Therapy Session",
            "Health Checkup", "Medication Refill", "Eye Exam", "Specialist Consultation"
        )
    )

    private val eventLocations = mapOf(
        "work" to listOf(
            "Conference Room A", "Meeting Room 3", "Office Building", "Head Office",
            "Client's Office", "Co-working Space", "Zoom Call", "Microsoft Teams",
            "Groww Vaishnavi Tech Park", "WeWork Downtown", "3rd Floor - Candle Room"
        ),
        "personal" to listOf(
            "Home", "Town Center", "Local Mall", "Downtown", "Friend's Place",
            "Coffee Shop", "Library", "Park", "Community Center"
        ),
        "family" to listOf(
            "Home", "School", "Community Center", "Park", "Restaurant",
            "Grandparents' House", "Movie Theater", "Zoo", "Museum"
        ),
        "fitness" to listOf(
            "Fitness Center", "City Gym", "Yoga Studio", "Swimming Pool",
            "Running Track", "Sports Complex", "Mountain Trail", "Beach"
        ),
        "travel" to listOf(
            "Airport", "Train Station", "Hotel Lobby", "City Center",
            "Tourist Information", "Resort", "Beach", "National Park"
        ),
        "study" to listOf(
            "Library", "Study Room", "University Campus", "Coffee Shop",
            "Home Office", "Classroom", "Conference Center", "Learning Lab"
        ),
        "finance" to listOf(
            "Bank", "Financial Advisor's Office", "Home Office", "Tax Office",
            "Investment Firm", "Accounting Department"
        ),
        "health" to listOf(
            "Doctor's Office", "Clinic", "Hospital", "Dental Clinic",
            "Therapy Center", "Medical Center", "Pharmacy", "Health Center"
        )
    )

    private val reminderTimes = listOf(0, 5, 10, 15, 30, 60, 120, 1440)

    override suspend fun fetchCalendarsForUser(userId: String): List<Calendar> {
        val random = Random(userId.hashCode())

        val colors = listOf(
            0xFF4285F4, // Blue
            0xFFDB4437, // Red
            0xFF0F9D58, // Green
            0xFFF4B400, // Yellow
            0xFF8560A8, // Purple
            0xFF03BCD4, // Cyan
            0xFFFF6D00, // Orange
            0xFF9C27B0, // Deep Purple
            0xFF795548, // Brown
            0xFF607D8B  // Blue Grey
        )

        val calendars = mutableListOf(
            Calendar(
                id = "${userId}_primary",
                name = "My Calendar",
                color = colors[0].toInt(),
                userId = userId,
                isPrimary = true
            ),
            Calendar(
                id = "${userId}_work",
                name = "Work",
                color = colors[1].toInt(),
                userId = userId
            ),
            Calendar(
                id = "${userId}_family",
                name = "Family",
                color = colors[2].toInt(),
                userId = userId
            )
        )

        val additionalCalendarTypes = calendarTypes.keys.toList() - listOf("work", "family")
        val numAdditional = random.nextInt(1, 4)
        val shuffledTypes = additionalCalendarTypes.shuffled(random)

        for (i in 0 until numAdditional) {
            if (i < shuffledTypes.size) {
                val calType = shuffledTypes[i]
                val colorIndex = random.nextInt(3, colors.size)

                calendars.add(
                    Calendar(
                        id = "${userId}_$calType",
                        name = calendarTypes[calType] ?: calType,
                        color = colors[colorIndex].toInt(),
                        userId = userId
                    )
                )
            }
        }

        return calendars
    }

    override suspend fun fetchEventsForCalendar(calendarId: String, startTime: Long, endTime: Long): List<Event> {
        val events = mutableListOf<Event>()
        val random = Random(calendarId.hashCode() + startTime)

        val calendarType = calendarId.substringAfter('_', "personal")

        val startInstant = Instant.fromEpochMilliseconds(startTime)
        val endInstant = Instant.fromEpochMilliseconds(endTime)
        val timeZone = TimeZone.currentSystemDefault()

        val currentDate = startInstant.toLocalDateTime(timeZone).date
        val endDate = endInstant.toLocalDateTime(timeZone).date

        if (calendarId.contains("work")) {
            events.addAll(generateWorkEvents(calendarId, startTime, endTime))
        }

        val daysInRange = (endDate.toEpochDays() - currentDate.toEpochDays()).toInt() + 1
        val eventDensity = when (calendarType) {
            "work" -> 0.7
            "family" -> 0.4
            "finance" -> 0.2
            else -> 0.3
        }

        val numberOfEvents = (daysInRange * eventDensity).toInt().coerceAtLeast(1)

        val titles = eventTitles[calendarType] ?: eventTitles["personal"]!!
        val locations = eventLocations[calendarType] ?: eventLocations["personal"]!!

        repeat(numberOfEvents) {
            val dayOffset = random.nextInt(daysInRange)
            val eventDate = currentDate.plus(DatePeriod(days = dayOffset))

            if (calendarType == "work" && eventDate.dayOfWeek.ordinal > 5) {
                return@repeat
            }

            val hour = random.nextInt(8, 20)
            val minute = (random.nextInt(4) * 15)

            val durationMinutes = random.nextInt(1, 5) * 30

            val eventStartDateTime = LocalDateTime(
                eventDate.year,
                eventDate.month,
                eventDate.day,
                hour,
                minute,
                0,
                0
            )

            val eventStartTimeMillis = eventStartDateTime.toInstant(timeZone).toEpochMilliseconds()
            val eventEndTimeMillis = eventStartTimeMillis + durationMinutes.minutes.inWholeMilliseconds

            val title = titles[random.nextInt(titles.size)]
            val location = if (random.nextInt(10) < 7) {
                locations[random.nextInt(locations.size)]
            } else null

            val isAllDay = random.nextInt(100) < when(calendarType) {
                "travel" -> 30
                "finance" -> 5
                else -> 10
            }

            val reminderCount = random.nextInt(3)
            val remindersList = if (reminderCount > 0) {
                List(reminderCount) { reminderTimes[random.nextInt(reminderTimes.size)] }.distinct()
            } else emptyList()

            val description = if (random.nextInt(2) == 0) {
                generateDescription(title, calendarType, random)
            } else null

            val event = Event(
                id = "event_${calendarId}_${eventStartTimeMillis}_${random.nextInt(1000)}",
                calendarId = calendarId,
                title = title,
                description = description,
                location = location,
                startTime = if (isAllDay) {
                    LocalDateTime(eventDate.year, eventDate.month, eventDate.day, 0, 0)
                        .toInstant(timeZone).toEpochMilliseconds()
                } else {
                    eventStartTimeMillis
                },
                endTime = if (isAllDay) {
                    LocalDateTime(eventDate.year, eventDate.month, eventDate.day, 23, 59)
                        .toInstant(timeZone).toEpochMilliseconds()
                } else {
                    eventEndTimeMillis
                },
                isAllDay = isAllDay,
                isRecurring = random.nextInt(20) == 0,
                recurringRule = null,
                reminderMinutes = remindersList,
                color = null
            )

            events.add(event)
        }

        return events
    }

    private fun generateWorkEvents(calendarId: String, startTime: Long, endTime: Long): List<Event> {
        val events = mutableListOf<Event>()
        val startInstant = Instant.fromEpochMilliseconds(startTime)
        val endInstant = Instant.fromEpochMilliseconds(endTime)
        val timeZone = TimeZone.currentSystemDefault()

        var currentDate = startInstant.toLocalDateTime(timeZone).date
        val endDate = endInstant.toLocalDateTime(timeZone).date

        while (currentDate <= endDate) {
            val dayOfWeek = currentDate.dayOfWeek
            if (dayOfWeek.ordinal in 1..5) {
                val eventStartDateTime = LocalDateTime(
                    currentDate.year,
                    currentDate.month,
                    currentDate.day,
                    11, 30, 0, 0
                )
                val eventStartTimeMillis = eventStartDateTime.toInstant(timeZone).toEpochMilliseconds()
                val eventEndTimeMillis = eventStartTimeMillis + 30.minutes.inWholeMilliseconds

                events.add(
                    Event(
                        id = "standup_${eventStartTimeMillis}",
                        calendarId = calendarId,
                        title = "MF Standup",
                        startTime = eventStartTimeMillis,
                        endTime = eventEndTimeMillis,
                        location = "Tech Park"
                    )
                )
            }

            currentDate = currentDate.plus(DatePeriod(days = 1))
        }

        currentDate = startInstant.toLocalDateTime(timeZone).date

        var biweeklyCounter = 0
        while (currentDate <= endDate) {
            val dayOfWeek = currentDate.dayOfWeek
            if (dayOfWeek == DayOfWeek.THURSDAY && biweeklyCounter % 2 == 0) {
                val eventStartDateTime = LocalDateTime(
                    currentDate.year,
                    currentDate.month,
                    currentDate.day,
                    14, 30, 0, 0
                )
                val eventStartTimeMillis = eventStartDateTime.toInstant(timeZone).toEpochMilliseconds()
                val eventEndTimeMillis = eventStartTimeMillis + 30.minutes.inWholeMilliseconds

                events.add(
                    Event(
                        id = "automation_${eventStartTimeMillis}",
                        calendarId = calendarId,
                        title = "App Automation biweekly",
                        startTime = eventStartTimeMillis,
                        endTime = eventEndTimeMillis,
                        location = "Tech Park 3rd Floor"
                    )
                )
            }

            if (dayOfWeek == DayOfWeek.SUNDAY) {
                biweeklyCounter++
            }
            currentDate = currentDate.plus(DatePeriod(days = 1))
        }

        currentDate = startInstant.toLocalDateTime(timeZone).date

        while (currentDate <= endDate) {
            val dayOfWeek = currentDate.dayOfWeek
            if (dayOfWeek == DayOfWeek.WEDNESDAY) {
                val eventStartDateTime = LocalDateTime(
                    currentDate.year,
                    currentDate.month,
                    currentDate.day,
                    16, 0, 0, 0
                )
                val eventStartTimeMillis = eventStartDateTime.toInstant(timeZone).toEpochMilliseconds()
                val eventEndTimeMillis = eventStartTimeMillis + 50.minutes.inWholeMilliseconds

                events.add(
                    Event(
                        id = "pitch_${eventStartTimeMillis}",
                        calendarId = calendarId,
                        title = "Product Pitch reviews [Candle 3rd floor]",
                        startTime = eventStartTimeMillis,
                        endTime = eventEndTimeMillis,
                        location = "Tech Park 3rd Floor-3rd Floor-Candle (20)"
                    )
                )
            }

            currentDate = currentDate.plus(DatePeriod(days = 1))
        }

        currentDate = startInstant.toLocalDateTime(timeZone).date

        while (currentDate <= endDate) {
            val dayOfWeek = currentDate.dayOfWeek
            if (dayOfWeek == DayOfWeek.FRIDAY) {
                val eventStartDateTime = LocalDateTime(
                    currentDate.year,
                    currentDate.month,
                    currentDate.day,
                    16, 30, 0, 0
                )
                val eventStartTimeMillis = eventStartDateTime.toInstant(timeZone).toEpochMilliseconds()
                val eventEndTimeMillis = eventStartTimeMillis + 30.minutes.inWholeMilliseconds

                events.add(
                    Event(
                        id = "fun_${eventStartTimeMillis}",
                        calendarId = calendarId,
                        title = "Fun fridays",
                        startTime = eventStartTimeMillis,
                        endTime = eventEndTimeMillis,
                        location = "Tech Park"
                    )
                )
            }

            currentDate = currentDate.plus(DatePeriod(days = 1))
        }

        return events
    }

    private fun generateDescription(title: String, calendarType: String, random: Random): String {
        val specialDescriptions = mapOf(
            "Team Meeting" to listOf(
                "Weekly sync to discuss project status and blockers.",
                "Quarterly planning session for team objectives.",
                "Review of team metrics and performance indicators."
            ),
            "Sprint Planning" to listOf(
                "Plan tasks for the upcoming two-week sprint.",
                "Review backlog items and estimate story points.",
                "Prioritize features and plan delivery timeline."
            ),
            "Doctor Appointment" to listOf(
                "Regular checkup with Dr. Johnson.",
                "Annual physical examination.",
                "Follow-up appointment for test results."
            ),
            "Gym Workout" to listOf(
                "Focus on upper body strength training.",
                "Cardio session - 30 min run, 15 min HIIT.",
                "Full body workout with trainer."
            )
        )

        specialDescriptions[title]?.let {
            return it[random.nextInt(it.size)]
        }

        val genericDescriptions = when (calendarType) {
            "work" -> listOf(
                "Don't forget to prepare the agenda beforehand.",
                "Bring your laptop and project notes.",
                "Meeting to discuss ${listOf("quarterly goals", "project timelines", "team performance", "upcoming deadlines").random(random)}.",
                "Virtual meeting - check email for link."
            )
            "fitness" -> listOf(
                "Remember to bring water and a towel.",
                "Wear appropriate workout clothes.",
                "Duration: ${listOf("30 minutes", "45 minutes", "1 hour", "1.5 hours").random(random)}."
            )
            "travel" -> listOf(
                "Check-in opens 24 hours before departure.",
                "Confirmation #: ${random.nextInt(100000, 1000000)}",
                "Bring passport and booking confirmation.",
                "Pack light, weather forecast: ${listOf("sunny", "rainy", "cloudy", "warm", "cold").random(random)}."
            )
            "health" -> listOf(
                "Bring insurance card and ID.",
                "Fasting required beforehand.",
                "Remember to ask about ${listOf("test results", "prescription refill", "referral", "symptoms").random(random)}."
            )
            else -> listOf(
                "Don't forget to set a reminder.",
                "Important event - mark as priority.",
                "Check details before attending.",
                "Add notes for preparation."
            )
        }

        return genericDescriptions[random.nextInt(genericDescriptions.size)]
    }

    override suspend fun fetchHolidays(countryCode: String, year: Int): List<Holiday> {
        val holidays = mutableListOf<Holiday>()
        val timeZone = TimeZone.currentSystemDefault()

        when (countryCode) {
            "IN" -> {
                holidays.add(createHoliday("republic_day_$year", "Republic Day", year, Month.JANUARY, 26, "IN", timeZone))
                holidays.add(createHoliday("shivaratri_$year", "Maha Shivaratri/Shivaratri", year, Month.FEBRUARY, 26, "IN", timeZone))
                holidays.add(createHoliday("holi_$year", "Holi", year, Month.MARCH, 9, "IN", timeZone))
                holidays.add(createHoliday("ramadan_$year", "Ramadan Start (tentative)", year, Month.MARCH, 2, "IN", timeZone))
                holidays.add(createHoliday("independence_day_$year", "Independence Day", year, Month.AUGUST, 15, "IN", timeZone))
                holidays.add(createHoliday("gandhi_jayanti_$year", "Gandhi Jayanti", year, Month.OCTOBER, 2, "IN", timeZone))
                holidays.add(createHoliday("diwali_$year", "Diwali", year, Month.NOVEMBER, 4, "IN", timeZone))
            }
            "US" -> {
                holidays.add(createHoliday("new_year_$year", "New Year's Day", year, Month.JANUARY, 1, "US", timeZone))
                holidays.add(createHoliday("mlk_day_$year", "Martin Luther King Jr. Day", year, Month.JANUARY, 17, "US", timeZone))
                holidays.add(createHoliday("presidents_day_$year", "Presidents' Day", year, Month.FEBRUARY, 21, "US", timeZone))
                holidays.add(createHoliday("memorial_day_$year", "Memorial Day", year, Month.MAY, 30, "US", timeZone))
                holidays.add(createHoliday("independence_day_$year", "Independence Day", year, Month.JULY, 4, "US", timeZone))
                holidays.add(createHoliday("labor_day_$year", "Labor Day", year, Month.SEPTEMBER, 5, "US", timeZone))
                holidays.add(createHoliday("thanksgiving_$year", "Thanksgiving", year, Month.NOVEMBER, 24, "US", timeZone))
                holidays.add(createHoliday("christmas_$year", "Christmas", year, Month.DECEMBER, 25, "US", timeZone))
            }
            "UK" -> {
                holidays.add(createHoliday("new_year_$year", "New Year's Day", year, Month.JANUARY, 1, "UK", timeZone))
                holidays.add(createHoliday("good_friday_$year", "Good Friday", year, Month.APRIL, 7, "UK", timeZone))
                holidays.add(createHoliday("easter_monday_$year", "Easter Monday", year, Month.APRIL, 10, "UK", timeZone))
                holidays.add(createHoliday("early_may_$year", "Early May Bank Holiday", year, Month.MAY, 1, "UK", timeZone))
                holidays.add(createHoliday("spring_bank_$year", "Spring Bank Holiday", year, Month.MAY, 29, "UK", timeZone))
                holidays.add(createHoliday("summer_bank_$year", "Summer Bank Holiday", year, Month.AUGUST, 28, "UK", timeZone))
                holidays.add(createHoliday("christmas_$year", "Christmas Day", year, Month.DECEMBER, 25, "UK", timeZone))
                holidays.add(createHoliday("boxing_day_$year", "Boxing Day", year, Month.DECEMBER, 26, "UK", timeZone))
            }
            "AU" -> {
                holidays.add(createHoliday("new_year_$year", "New Year's Day", year, Month.JANUARY, 1, "AU", timeZone))
                holidays.add(createHoliday("australia_day_$year", "Australia Day", year, Month.JANUARY, 26, "AU", timeZone))
                holidays.add(createHoliday("good_friday_$year", "Good Friday", year, Month.APRIL, 7, "AU", timeZone))
                holidays.add(createHoliday("easter_monday_$year", "Easter Monday", year, Month.APRIL, 10, "AU", timeZone))
                holidays.add(createHoliday("anzac_day_$year", "Anzac Day", year, Month.APRIL, 25, "AU", timeZone))
                holidays.add(createHoliday("christmas_$year", "Christmas Day", year, Month.DECEMBER, 25, "AU", timeZone))
                holidays.add(createHoliday("boxing_day_$year", "Boxing Day", year, Month.DECEMBER, 26, "AU", timeZone))
            }
            "RU" -> {
                val loveDay = LocalDateTime(year, Month.FEBRUARY, 14, 0, 0)
                holidays.add(
                    Holiday(
                        id = "loveDay_$year",
                        name = "Love Day",
                        date = loveDay.toInstant(timeZone).toEpochMilliseconds(),
                        countryCode = "RU"
                    )
                )
                val mensDay = LocalDateTime(year, Month.FEBRUARY, 23, 0, 0)
                holidays.add(
                    Holiday(
                        id = "mensDay_$year",
                        name = "Mens Day",
                        date = mensDay.toInstant(timeZone).toEpochMilliseconds(),
                        countryCode = "RU"
                    )
                )
                val womenDay = LocalDateTime(year, Month.MARCH, 8, 0, 0)
                holidays.add(
                    Holiday(
                        id = "womenDay_$year",
                        name = "Women Day",
                        date = womenDay.toInstant(timeZone).toEpochMilliseconds(),
                        countryCode = "RU"
                    )
                )
                val newYear = LocalDateTime(year, Month.JANUARY, 1, 0, 0)
                holidays.add(
                    Holiday(
                        id = "newYear_$year",
                        name = "New Year",
                        date = newYear.toInstant(timeZone).toEpochMilliseconds(),
                        countryCode = "RU"
                    )
                )
            }
        }

        return holidays
    }

    private fun createHoliday(
        id: String,
        name: String,
        year: Int,
        month: Month,
        day: Int,
        countryCode: String,
        timeZone: TimeZone
    ): Holiday {
        val date = LocalDateTime(year, month, day, 0, 0)
        return Holiday(
            id = id,
            name = name,
            date = date.toInstant(timeZone).toEpochMilliseconds(),
            countryCode = countryCode
        )
    }
}