package golem

import golem.util.assertMatrixEquals
import org.junit.Test

class matrixfuncs_tests {
    @Test
    fun testExpm()
    {
        assertMatrixEquals(expm(eye(3)), E*eye(3))

        val expected = mat[20.08553692318771,   0,                 0 end
                0,                   20.08553692318771, 0 end
                0,                   0,                 20.08553692318771]
        assertMatrixEquals(expm(eye(3)*3), expected)
    }
}