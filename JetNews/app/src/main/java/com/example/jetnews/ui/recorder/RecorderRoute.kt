package com.example.jetnews.ui.recorder

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Stateful composable that displays the Navigation route for the Interests screen.
 *
 * @param recorderViewModel ViewModel that handles the business logic of this screen
 * @param isExpandedScreen (state) true if the screen is expanded
 * @param openDrawer (event) request opening the app drawer
 * @param snackbarHostState (state) state for screen snackbar host
 */
@Composable
fun RecorderRoute(
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {

    RecorderScreen(
        isExpandedScreen = isExpandedScreen,
        openDrawer = openDrawer,
        snackbarHostState = snackbarHostState
    )
}