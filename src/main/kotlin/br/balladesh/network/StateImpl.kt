package br.balladesh.network

class NetworkRequest {
  private val machine = NetworkMachine()

  fun startRequest() {
    this.machine.start()
  }
}

class NetworkMachine : Machine {
  val allStates = AllStates(this)
  private var currentState: NetworkState = this.allStates.initialRequest

  override fun start() {
    this.currentState.fireState()
  }

  override fun setAndFireState(state: NetworkState) {
    this.currentState = state
    this.currentState.fireState()
  }
}

data class AllStates(val networkMachine: NetworkMachine) {
  val initialRequest = InitRequest(networkMachine)
  val refreshToken = RefreshToken(networkMachine)
  val retryRequest = RetryRequest(networkMachine)
  val failRequest = FailRequest(networkMachine)
  val successfulRequest = SuccessfulRequest(networkMachine)
}

class InitRequest(private val networkMachine: NetworkMachine) : NetworkState {
  override fun fireState() {
    println("Starting the request")
    println("The request failed with 403: Forbidden")
    this.onForbidden()
  }

  override fun onForbidden() {
    networkMachine.setAndFireState(networkMachine.allStates.refreshToken)
  }

  override fun onSuccess() {
    networkMachine.setAndFireState(networkMachine.allStates.successfulRequest)
  }

  override fun onFail() {
    networkMachine.setAndFireState(networkMachine.allStates.failRequest)
  }
}

class RefreshToken(private val networkMachine: NetworkMachine) : NetworkState {
  override fun fireState() {
    println("Starting the refresh")
    println("The token was refreshed, retrying transaction")
    this.onSuccess()
  }

  override fun onForbidden() {
    networkMachine.setAndFireState(networkMachine.allStates.failRequest)
  }

  override fun onSuccess() {
    networkMachine.setAndFireState(networkMachine.allStates.retryRequest)
  }

  override fun onFail() {
    networkMachine.setAndFireState(networkMachine.allStates.failRequest)
  }
}

class RetryRequest(private val networkMachine: NetworkMachine) : NetworkState {
  override fun fireState() {
    println("Trying to retry the connection again, waiting")
    println("The request was successful")
    this.onSuccess()
  }

  override fun onForbidden() {
    networkMachine.setAndFireState(networkMachine.allStates.failRequest)
  }

  override fun onSuccess() {
    networkMachine.setAndFireState(networkMachine.allStates.successfulRequest)
  }

  override fun onFail() {
    networkMachine.setAndFireState(networkMachine.allStates.failRequest)
  }
}

class FailRequest(private val networkMachine: NetworkMachine) : NetworkState {
  override fun fireState() {
    println("The request failed. Finished")
    this.onFail()
  }

  override fun onForbidden() {
    // Do nothing
  }

  override fun onSuccess() {
    // Do nothing
  }

  override fun onFail() {
    // Do nothing
  }
}

class SuccessfulRequest(private val networkMachine: NetworkMachine) : NetworkState {
  override fun fireState() {
    println("The request was successful. Finished")
    this.onSuccess()
  }

  override fun onForbidden() {
    // Do nothing
  }

  override fun onSuccess() {
    // Do nothing
  }

  override fun onFail() {
    // Do nothing
  }
}
