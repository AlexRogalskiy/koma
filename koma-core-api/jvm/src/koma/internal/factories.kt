package koma.internal

import koma.internal.default.generated.matrix.DefaultFloatMatrixFactory
import koma.internal.default.generated.matrix.DefaultIntMatrixFactory
import koma.matrix.*

// TODO: Replace with ServiceLoader


internal actual fun getDoubleFactories(): List<MatrixFactory<Matrix<Double>>> {
    val facCandidates = arrayOf("koma.matrix.mtj.MTJMatrixFactory",
                                "koma.matrix.ejml.EJMLMatrixFactory",
                                "koma.matrix.jblas.JBlasMatrixFactory")
    val out: MutableList<MatrixFactory<Matrix<Double>>> = ArrayList()

    facCandidates.forEach {
        try {
            val fac: Class<*> = Class.forName(it)
            fac.constructors.forEach { ctor ->
                if (ctor.parameterTypes.isEmpty()) {
                    @Suppress("UNCHECKED_CAST")
                    val inst = ctor.newInstance() as MatrixFactory<Matrix<Double>>
                    // Actual classpath search
                    inst.zeros(1, 1)
                    // Backend exists
                    out.add(inst)
                }
            }
        }
        catch(e: ClassNotFoundException) {
        }
        catch(e: NoClassDefFoundError) {
        }
    }
    return out
}


actual fun getDoubleFactory(): MatrixFactory<Matrix<Double>> {
    val facs = getFactories<Double>()
    if (facs.isNotEmpty())
        return facs[0]
    error("No double matrix factories available. (Did you forget to import a koma-core implementation?)")
}
actual fun getFloatFactory(): MatrixFactory<Matrix<Float>> = DefaultFloatMatrixFactory()
actual fun getIntFactory(): MatrixFactory<Matrix<Int>> = DefaultIntMatrixFactory()


internal fun <T> getFactories(): List<MatrixFactory<Matrix<T>>> {
    val facCandidates = arrayOf("koma.matrix.mtj.MTJMatrixFactory",
            "koma.matrix.ejml.EJMLMatrixFactory",
            "koma.matrix.jblas.JBlasMatrixFactory",
            "koma.matrix.default.DefaultFloatMatrix",
            "koma.matrix.default.DefaultIntMatrix",
            "koma.matrix.default.DefaultDoubleMatrix",
            "koma.matrix.default.DefaultLongMatrix")
    val out: MutableList<MatrixFactory<Matrix<T>>> = ArrayList()

    facCandidates.forEach {
        try {
            val fac: Class<*> = Class.forName(it)
            fac.constructors.forEach { ctor ->
                if (ctor.parameterTypes.isEmpty()) {
                    @Suppress("UNCHECKED_CAST")
                    val inst = ctor.newInstance() as MatrixFactory<Matrix<T>>
                    // Actual classpath search
                    inst.zeros(1, 1)
                    // Backend exists
                    out.add(inst)
                }
            }
        }
        catch(e: ClassNotFoundException) {
        }
        catch(e: NoClassDefFoundError) {
        }
    }
    return out
}