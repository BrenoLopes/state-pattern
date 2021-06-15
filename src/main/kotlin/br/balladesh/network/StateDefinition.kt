package br.balladesh.network

interface Machine {
  fun start()
  fun setAndFireState(state: NetworkState)
}

interface NetworkState {
  fun fireState()
  fun onForbidden()
  fun onSuccess()
  fun onFail()
}
