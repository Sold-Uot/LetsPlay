package ru.radixit.letsplay.utils
typealias getState  = (Unit) -> Unit
data class LoadState<out T>(val status: Status, val data: T?, val message: String?) {


    companion object {

        fun <T> success(data: T?): LoadState<T> {
            return LoadState(Status.SUCCESS, data, null)
        }



        fun <T> error(msg: String): LoadState<T> {
            return LoadState(Status.ERROR, null, msg)
        }


        fun <T> loading(): LoadState<T> {
            return LoadState(Status.LOADING, null, null)
        }
    }

}


enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}