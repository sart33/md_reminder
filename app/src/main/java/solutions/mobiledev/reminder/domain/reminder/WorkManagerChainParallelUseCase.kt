package solutions.mobiledev.reminder.domain.reminder

import androidx.work.WorkRequest
import solutions.mobiledev.reminder.domain.repository.ReminderRepository

class WorkManagerChainParallelUseCase(
    private val reminderRepository: ReminderRepository

) {
//    operator fun invoke(workRequest1: WorkRequest) {
//        return reminderRepository.workManagerChainParallel(workRequest1)
//    }
//    operator fun invoke(workRequest1: WorkRequest, workRequest2: WorkRequest) {
//        return reminderRepository.workManagerChainParallel(workRequest1, workRequest2)
//    }
    operator fun invoke(workRequestList: MutableList<WorkRequest>) {
        return reminderRepository.workManagerChainParallel(workRequestList)
    }
}