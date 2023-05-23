package com.wordsfairy.note.ext.flowbus

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * @author 李雄厚
 *
 * https://zhuanlan.zhihu.com/p/451951619
 */
class FlowBus : ViewModel() {
    companion object {
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { FlowBus() }
    }

    //正常事件
    private val events = mutableMapOf<String, Event<*>>()

    //粘性事件
    private val stickyEvents = mutableMapOf<String, Event<*>>()

    fun with(key: String, isSticky: Boolean = false): Event<Any> {
        return with(key, Any::class.java, isSticky)
    }

    fun <T> with(eventType: Class<T>, isSticky: Boolean = false): Event<T> {
        return with(eventType.name, eventType, isSticky)
    }

    @Synchronized
    fun <T> with(key: String, type: Class<T>?, isSticky: Boolean): Event<T> {
        val flows = if (isSticky) stickyEvents else events
        if (!flows.containsKey(key)) {
            flows[key] = Event<T>(key, isSticky)
        }
        return flows[key] as Event<T>
    }


    class Event<T>(private val key: String, isSticky: Boolean) {

        // private mutable shared flow
        private val _events = MutableSharedFlow<T>(
            replay = if (isSticky) 1 else 0,
            extraBufferCapacity = Int.MAX_VALUE
        )

        // publicly exposed as read-only shared flow
        private val events = _events.asSharedFlow()

        /**
         * need main thread execute
         */
        fun observeEvent(
            lifecycleOwner: LifecycleOwner,
            dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
            minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
            action: (t: T) -> Unit
        ) {
            lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    val subscriptCount = _events.subscriptionCount.value
                    if (subscriptCount <= 0)
                        instance.events.remove(key)
                }
            })
            lifecycleOwner.lifecycleScope.launch(dispatcher) {
                lifecycleOwner.lifecycle.whenStateAtLeast(minActiveState) {
                    events.collect {
                        try {
                            action(it)
                        } catch (e: Exception) {
                        }
                    }
                }
            }
        }

        /**
         * send value
         */
        suspend fun setValue(
            event: T,
            dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate
        ) {
            withContext(dispatcher) {
                _events.emit(event)
            }

        }
    }
}

fun LifecycleOwner.observeEvent(
    dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    isSticky: Boolean = false,
    key: String,
    action: (t: Any) -> Unit
) {
    ApplicationScopeViewModelProvider
        .getApplicationScopeViewModel(FlowBus::class.java)
        .with(key, isSticky = isSticky)
        .observeEvent(this@observeEvent, dispatcher, minActiveState, action)
}

fun Any.postEventValue(
    key: String,
    event: Any,
    delayTimeMillis: Long = 0,
    isSticky: Boolean = false,
    dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) {
    ApplicationScopeViewModelProvider
        .getApplicationScopeViewModel(FlowBus::class.java)
        .viewModelScope
        .launch(dispatcher) {
            delay(delayTimeMillis)
            ApplicationScopeViewModelProvider
                .getApplicationScopeViewModel(FlowBus::class.java)
                .with(key, isSticky = isSticky)
                .setValue(event)
        }
}

private object ApplicationScopeViewModelProvider : ViewModelStoreOwner {

    private val eventViewModelStore: ViewModelStore = ViewModelStore()

    private val mApplicationProvider: ViewModelProvider by lazy {
        ViewModelProvider(
            ApplicationScopeViewModelProvider,
            ViewModelProvider.AndroidViewModelFactory.getInstance(FlowBusInitializer.application)
        )
    }

    fun <T : ViewModel> getApplicationScopeViewModel(modelClass: Class<T>): T {
        return mApplicationProvider[modelClass]
    }

    override val viewModelStore: ViewModelStore = eventViewModelStore

}

object FlowBusInitializer {
    lateinit var application: Application

    //在Application中初始化
    fun init(application: Application) {
        FlowBusInitializer.application = application
    }
}