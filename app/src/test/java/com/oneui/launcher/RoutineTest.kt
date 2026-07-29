package com.oneui.launcher

import com.oneui.launcher.routines.Routine
import com.oneui.launcher.routines.TriggerType
import com.oneui.launcher.routines.ActionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RoutineTest {

    @Test
    fun testRoutineCreation() {
        val routine = Routine(
            id = "1",
            name = "WLAN Auto",
            triggerType = TriggerType.WIFI,
            triggerValue = "HomeWiFi",
            actionType = ActionType.VOLUME,
            actionValue = "80",
            isEnabled = true
        )

        assertEquals("1", routine.id)
        assertEquals("WLAN Auto", routine.name)
        assertEquals(TriggerType.WIFI, routine.triggerType)
        assertEquals("HomeWiFi", routine.triggerValue)
        assertEquals(ActionType.VOLUME, routine.actionType)
        assertEquals("80", routine.actionValue)
        assertTrue(routine.isEnabled)
    }
}
