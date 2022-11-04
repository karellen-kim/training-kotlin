# Kotlin

## Coroutine

### 비동기 함수
* 비동기 함수는 두가지 종류가 있다.
1. Job
  * 결과 없음. 결과를 대기하지 않는다. fire and forget
  * `launch`로 생성한다
  * `join`을 사용하면 결과를 기다린다
2. Deferred
  * 결과 반환함
  * Future, Promise의 구현체
  * `async`로 생성
  * `launch`, `async`에 `start=CoroutineStart.LAZY` 옵션을 주면 lazy하게 시작할 수 있다

### Job Life cycle

<img width="600" src="img/lifecycle.png">

|  State |  isActive |  isCompleted |  isCancelled |
|---|---|---|---|
|  New (optional initial state) |  false |  false |  false |
|  Active (default initial state) |  true |  false |  false |
|  Completing (transient state) |  true |  false |  false |
|  Cancelling (transient state) |  false |  false |  true |
|  Cancelled (final state) |  false |  true |  true |
|  Completed (final state) |  false |  true |  false |

  * 상태는 한 방향으로만 이동한다. 최종 상태는 Cancelled, Completed다. 완료되면 재시작 할 수 없다.

