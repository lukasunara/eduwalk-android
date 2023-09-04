package hr.eduwalk.ui.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.eduwalk.data.model.Walk
import hr.eduwalk.networking.EduWalkRepository
import hr.eduwalk.ui.event.EditWalkEvent
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class EditWalkViewModel @Inject constructor(
    private val eduWalkRepository: EduWalkRepository,
) : BaseViewModel<Unit, EditWalkEvent>() {

    override val uiStateFlow = MutableStateFlow(value = Unit)
    override val eventsFlow = MutableStateFlow<EditWalkEvent?>(value = null)

    private var walk: Walk? = null
    private var walkId: String? = null

    fun start(walk: Walk?) {
        walk?.let {
            this.walk = it
            walkId = it.id
        } ?: generateNewUniqueWalkId()
    }

    fun onEditWalkClicked(walkTitle: String, walkDescription: String?) {
        viewModelScope.launch {
            val newWalk = Walk(
                id = walkId!!,
                title = walkTitle,
                description = walkDescription,
                creatorId = eduWalkRepository.getUser()!!.username,
            )
            when (walk) {
                null -> {
                    handleErrorResponse(response = eduWalkRepository.createWalk(walk = newWalk)) ?: return@launch
                    eventsFlow.emit(value = EditWalkEvent.FinishCreatingWalk(walk = newWalk))
                }
                else -> {
                    handleErrorResponse(
                        response = eduWalkRepository.updateWalkInfo(
                            walkId = newWalk.id,
                            walkTitle = newWalk.title,
                            walkDescription = newWalk.description,
                        )
                    ) ?: return@launch
                    eventsFlow.emit(value = EditWalkEvent.FinishEditingWalkInfo(walk = newWalk))
                }
            }
        }
    }

    private fun generateNewUniqueWalkId() {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')

        val newWalkId = (1..WALK_ID_LENGTH).map { allowedChars.random() }.joinToString("")

        this.walkId = newWalkId
        viewModelScope.launch {
            eventsFlow.emit(value = EditWalkEvent.SetupWalkId(walkId = newWalkId))
        }
    }

    private companion object {
        const val WALK_ID_LENGTH = 8
    }
}
