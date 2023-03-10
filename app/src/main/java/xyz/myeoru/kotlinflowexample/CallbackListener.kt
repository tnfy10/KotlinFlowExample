package xyz.myeoru.kotlinflowexample

interface CallbackListener {
    fun onResult(result: String)
}

class CallbackCaller(private val listener: CallbackListener) {
    fun call() {
        listener.onResult("This is result")
    }
}
