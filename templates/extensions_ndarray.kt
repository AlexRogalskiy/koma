@file:koma.internal.JvmName("NDArray")
@file:koma.internal.JvmMultifileClass

/**
 * THIS FILE IS AUTOGENERATED, DO NOT MODIFY. EDIT THE FILES IN templates/
 * AND RUN ./gradlew :codegen INSTEAD!
 */

package koma.extensions

import koma.internal.default.generated.ndarray.DefaultGenericNDArray
import koma.internal.default.utils.checkIndices
import koma.internal.default.utils.linearToNIdx
import koma.matrix.doubleFactory
import koma.ndarray.NDArray
import koma.ndarray.${factoryPrefix}NDArrayFactory
import koma.internal.default.utils.nIdxToLinear
import koma.pow
import koma.matrix.Matrix

$toMatrix

@koma.internal.JvmName("fill${dtypeName}")
${inline}fun ${genDec} NDArray<${dtype}>.fill(f: (idx: IntArray) -> ${dtype}) = apply {
    for ((nd, linear) in this.iterateIndices())
        this.set${dtypeName}(linear, f(nd))
}

@koma.internal.JvmName("fill${dtypeName}Both")
${inline}fun ${genDec} NDArray<${dtype}>.fillBoth(f: (nd: IntArray, linear: Int) -> ${dtype}) = apply {
    for ((nd, linear) in this.iterateIndices())
        this.set${dtypeName}(linear, f(nd, linear))
}

@koma.internal.JvmName("fill${dtypeName}Linear")
${inline}fun ${genDec} NDArray<${dtype}>.fillLinear(f: (idx: Int) -> ${dtype}) = apply {
    for (idx in 0 until size)
        this.set${dtypeName}(idx, f(idx))
}

@koma.internal.JvmName("create${dtypeName}")
${inline}fun ${genDec} ${factoryPrefix}NDArrayFactory<${dtype}>.create(vararg lengths: Int, filler: (idx: IntArray) -> ${dtype})
$extensionCreate

/**
 * Returns an array with the same data, but shaped differently.
 */
@koma.internal.JvmName("reshape${dtypeName}")
${reifiedInline}fun $reifiedDec NDArray<${dtype}>.reshape(vararg newShape: Int): NDArray<${dtype}> {
    if (newShape.reduce { a, b -> a * b } != size)
        throw IllegalArgumentException("NDArray with \$size items cannot be reshaped to \${newShape.toList()}")
    var idx = 0
    return NDArray(*newShape) { _ -> get${dtypeName}(idx++) }
}


/**
 * Takes each element in a NDArray, passes them through f, and puts the output of f into an
 * output NDArray.
 *
 * @param f A function that takes in an element and returns an element
 *
 * @return the new NDArray after each element is mapped through f
 */
@koma.internal.JvmName("map${dtypeName}")
${inline}fun ${genDec} NDArray<${dtype}>.map(f: (${dtype}) -> ${dtype})
$extensionMap

/**
 * Takes each element in a NDArray, passes them through f, and puts the output of f into an
 * output NDArray. Index given to f is a linear index, depending on the underlying storage
 * major dimension.
 *
 * @param f A function that takes in an element and returns an element. Function also takes
 *      in the linear index of the element's location.
 *
 * @return the new NDArray after each element is mapped through f
 */
@koma.internal.JvmName("mapIndexed${dtypeName}")
${inline}fun ${genDec} NDArray<${dtype}>.mapIndexed(f: (idx: Int, ele: ${dtype}) -> ${dtype})
$extensionMapIndexed

/**
 * Takes each element in a NDArray and passes them through f.
 *
 * @param f A function that takes in an element
 *
 */
@koma.internal.JvmName("forEach${dtypeName}")
${inline}fun ${genDec} NDArray<${dtype}>.forEach(f: (ele: ${dtype}) -> Unit) {
    // TODO: Change this back to iteration once there are non-boxing iterators
    for (idx in 0 until size)
        f(get${dtypeName}(idx))
}
/**
 * Takes each element in a NDArray and passes them through f. Index given to f is a linear
 * index, depending on the underlying storage major dimension.
 *
 * @param f A function that takes in an element. Function also takes
 *      in the linear index of the element's location.
 *
 */
@koma.internal.JvmName("forEachIndexed${dtypeName}")
${inline}fun $genDec NDArray<${dtype}>.forEachIndexed(f: (idx: Int, ele: ${dtype}) -> Unit) {
    // TODO: Change this back to iteration once there are non-boxing iterators
    for (idx in 0 until size)
        f(idx, get${dtypeName}(idx))
}

/**
 * Takes each element in a NDArray, passes them through f, and puts the output of f into an
 * output NDArray. Index given to f is the full ND index of the element.
 *
 * @param f A function that takes in an element and returns an element. Function also takes
 *      in the ND index of the element's location.
 *
 * @return the new NDArray after each element is mapped through f
 */
@koma.internal.JvmName("mapIndexedN${dtypeName}")
${inline}fun $genDec NDArray<${dtype}>.mapIndexedN(f: (idx: IntArray, ele: ${dtype}) -> ${dtype}): NDArray<${dtype}>
$extensionMapIndexedN

/**
 * Takes each element in a NDArray and passes them through f. Index given to f is the full
 * ND index of the element.
 *
 * @param f A function that takes in an element. Function also takes
 *      in the ND index of the element's location.
 *
 */
@koma.internal.JvmName("forEachIndexedN${dtypeName}")
${inline}fun $genDec NDArray<${dtype}>.forEachIndexedN(f: (idx: IntArray, ele: ${dtype}) -> Unit) {
    for ((nd, linear) in iterateIndices())
        f(nd, get${dtypeName}(linear))
}

/**
 * Converts this NDArray into a one-dimensional ${arrayClass} in row-major order.
 */
${reifiedInline}fun $reifiedDec NDArray<${dtype}>.to${arrayType}Array() = ${arrayClass}(size) { get${dtypeName}(it) }

@koma.internal.JvmName("getRanges${dtypeName}")
operator fun $genDec NDArray<${dtype}>.get(vararg indices: IntRange): NDArray<${dtype}> {
    checkIndices(indices.map { it.last }.toIntArray())
    return DefaultGenericNDArray<${dtype}>(shape = *indices
            .map { it.last - it.first + 1 }
            .toIntArray()) { newIdxs ->
        val offsets = indices.map { it.first }
        val oldIdxs = newIdxs.zip(offsets).map { it.first + it.second }
        this.getGeneric(*oldIdxs.toIntArray())
    }
}

@koma.internal.JvmName("set${dtypeName}")
operator fun $genDec NDArray<${dtype}>.set(vararg indices: Int, value: NDArray<${dtype}>) {
    val shape = shape()
    val lastIndex = indices.mapIndexed { i, range -> range + value.shape()[i] }
    val outOfBounds = lastIndex.withIndex().any { it.value > shape()[it.index] }
    if (outOfBounds)
        throw IllegalArgumentException("NDArray with shape \${shape()} cannot be " +
                "set at \${indices.toList()} by a \${value.shape()} array " +
                "(out of bounds)")

    val offset = indices.map { it }.toIntArray()
    value.forEachIndexedN { idx, ele ->
        val newIdx = offset.zip(idx).map { it.first + it.second }.toIntArray()
        this.setGeneric(indices=*newIdx, v=ele)
    }
}


operator fun $genDec NDArray<${dtype}>.get(vararg indices: Int) = get${dtypeName}(*indices)
operator fun $genDec NDArray<${dtype}>.set(vararg indices: Int, value: ${dtype}) = set${dtypeName}(indices=*indices, v=value)

$operators
