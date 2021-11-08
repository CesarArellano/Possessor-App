package com.cesararellano.possessorapp

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Color
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

// Implementamos la clase abstracta que hereda la clase ItemTouchHelper, dicha clase será la que gestione los gestos que haga el usuario a los items del RecyclerView.
abstract class RecyclerViewGestures : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
    ItemTouchHelper.LEFT) {

    // onChildDraw es un método propio del ItemTouchHelper, que nos ayudará a decorar el swipe para eliminar la cosa.
    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        // El método Builder de RecyclerViewSwipeDecorator, es un método de una librería externa que ayuda a mejorar la interacción nativa del swipe delete.
        RecyclerViewSwipeDecorator.Builder(
            canvas,
            recyclerView,
            viewHolder,
            dX,
            dY,
            actionState,
            isCurrentlyActive
        )
            .addSwipeLeftBackgroundColor(Color.RED)
            .addSwipeLeftActionIcon(R.drawable.ic_delete)
            .create()
            .decorate()

        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}