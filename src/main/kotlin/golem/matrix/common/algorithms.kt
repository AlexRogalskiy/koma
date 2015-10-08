package golem.matrix.common

import golem.*
import golem.matrix.Matrix

/**
 * A backend agnostic implementation of expm. Since the only things that are
 * backend specific are the linear system solver and P=1 norm provider, these
 * things must be passed in (generally from the backends implementation).
 */
fun expm(matrix: Matrix<Double>,
         normProvider: (Matrix<Double>)->Double,
         solveProvider: (Matrix<Double>, Matrix<Double>)->Matrix<Double>)
        : Matrix<Double> {
    var A = matrix
    var A_L1 = normProvider(A)
    var n_squarings = 0.0

    // Spread returns so we can val(U,V) here (TODO: Fix this when Kotlin allows)

    if (A_L1 < 1.495585217958292e-002) {
        val (U, V) = _pade3(A)
        return dispatchPade(U,V, n_squarings, solveProvider)
    }
    else if (A_L1 < 2.539398330063230e-001) {
        val (U, V) = _pade5(A)
        return dispatchPade(U,V, n_squarings, solveProvider)
    }
    else if (A_L1 < 9.504178996162932e-001) {
        val (U, V) = _pade7(A)
        return dispatchPade(U,V, n_squarings, solveProvider)
    }
    else if (A_L1 < 2.097847961257068e+000) {
        val (U, V) = _pade9(A)
        return dispatchPade(U,V, n_squarings, solveProvider)
    }
    else {

        var maxnorm = 5.371920351148152
        n_squarings = golem.max(0.0, ceil(logb(2, A_L1 / maxnorm))).toDouble() //
        A /= pow(2.0, n_squarings)
        val (U, V) = _pade13(A)
        return dispatchPade(U, V, n_squarings, solveProvider)
    }

}
private fun dispatchPade(U: Matrix<Double>,
                         V: Matrix<Double>,
                         n_squarings: Double,
                         solveProvider: (Matrix<Double>, Matrix<Double>)->Matrix<Double>): Matrix<Double>
{
    var P = U+V
    var Q = -U+V
    //var R = solve(Q,P)
    var R = U.getFactory().zeros(Q.numCols(), P.numCols())
    for (i in 0..P.numCols()-1) {
        R.setCol(i, solveProvider(Q, P.getCol(i)))

    }
    for (i in 0..n_squarings-1)
        R *= R
    return R
}

private fun _pade3(A: Matrix<Double>): Pair<Matrix<Double>, Matrix<Double>>
{
    var b = golem.mat[120, 60, 12, 1]
    var ident = A.getFactory().eye(A.numRows(), A.numCols())

    var A2 = A*A
    var U = A*(A2*b[3]+ident*b[1])
    var V = A2*b[2] + ident*b[0]

    return Pair(U,V)
}
private fun _pade5(A: Matrix<Double>): Pair<Matrix<Double>, Matrix<Double>>
{
    var b = golem.mat[30240, 15120, 3360, 420, 30, 1]
    var ident = A.getFactory().eye(A.numRows(), A.numCols())
    var A2 = A*A
    var A4 = A2*A2
    var U = A*(A4*b[5]+A2*b[3]+ident*b[1])
    var V = A4*b[4]+A2*b[2]+ident*b[0]
    return Pair(U,V)

}
private fun _pade7(A: Matrix<Double>): Pair<Matrix<Double>, Matrix<Double>>
{
    var b = golem.mat[17297280, 8648640, 1995840, 277200, 25200, 1512, 56, 1]
    var ident = A.getFactory().eye(A.numRows(), A.numCols())
    var A2 = A*A
    var A4 = A2*A2
    var A6 = A4*A2
    var U = A*(A6*b[7]+A4*b[5]+A2*b[3]+ident*b[1])
    var V = A6*b[6]+A4*b[4]+A2*b[2]+ident*b[0]
    return Pair(U,V)
}
private fun _pade9(A: Matrix<Double>): Pair<Matrix<Double>, Matrix<Double>>
{
    var b = golem.mat[17643225600, 8821612800, 2075673600, 302702400, 30270240,
            2162160, 110880, 3960, 90, 1]
    var ident = A.getFactory().eye(A.numRows(), A.numCols())
    var A2 = A*A
    var A4 = A2*A2
    var A6 = A4*A2
    var A8 = A6*A2
    var U = A*(A8*b[9] + A6*b[7] + A4*b[5] + A2*b[3] + ident*b[1])
    var V = A8*b[8] + A6*b[6] + A4*b[4] + A2*b[2] + ident*b[0]
    return Pair(U,V)
}
private fun _pade13(A: Matrix<Double>): Pair<Matrix<Double>, Matrix<Double>>
{
    var b = golem.mat[64764752532480000, 32382376266240000, 7771770303897600,
            1187353796428800, 129060195264000, 10559470521600, 670442572800,
            33522128640, 1323241920, 40840800, 960960, 16380, 182, 1]
    var ident = A.getFactory().eye(A.numRows(), A.numCols())

    var A2 = A*A
    var A4 = A2*A2
    var A6 = A4*A2
    var U = A*(A6*(A6*b[13] + A4*b[11] + A2*b[9]) + A6*b[7] + A4*b[5] + A2*b[3] + ident*b[1])
    var V = A6*(A6*b[12] + A4*b[10] + A2*b[8]) + A6*b[6] + A4*b[4] + A2*b[2] + ident*b[0]
    return Pair(U,V)
}
