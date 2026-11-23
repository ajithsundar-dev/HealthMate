package com.ajith.healthmate.data

object DummyData {

    suspend fun populateIfEmpty(repository: HealthRepository) {
        if (repository.getCount() > 0) {
            return
        }

        val records = listOf(
            HealthRecord(
                date = "2026-10-25",
                steps = 6500,
                calories = 320,
                water = 1800
            ),
            HealthRecord(
                date = "2026-10-26",
                steps = 8200,
                calories = 410,
                water = 2200
            ),
            HealthRecord(
                date = "2026-10-27",
                steps = 5100,
                calories = 250,
                water = 1600
            ),
            HealthRecord(
                date = "2026-10-28",
                steps = 10000,
                calories = 500,
                water = 2500
            ),
            HealthRecord(
                date = "2026-10-29",
                steps = 7400,
                calories = 370,
                water = 2000
            ),
            HealthRecord(
                date = "2026-10-30",
                steps = 9300,
                calories = 460,
                water = 2400
            ),
            HealthRecord(
                date = "2026-10-31",
                steps = 6800,
                calories = 340,
                water = 1900
            )
        )

        repository.insertAll(records)
    }
}