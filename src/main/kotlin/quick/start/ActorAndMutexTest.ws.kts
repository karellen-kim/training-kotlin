
    // 0.1+0.1+0.1 값을 float로 계산하여 출력합니다.
    val numFloat = (0.1 + 0.1 + 0.1).toFloat()
    println("float 값: $numFloat")

    // float 값을 이진 부동 소수점 형식으로 변환하여 출력합니다.
    val numBits = numFloat.toBits()
    println("float 값의 이진 부동 소수점 표현: ${Integer.toBinaryString(numBits)}")

    // 이진 부동 소수점 값을 16진수로 변환하여 출력합니다.
    val numHex = Integer.toHexString(numBits)
    println("float 값의 16진수 표현: $numHex")
