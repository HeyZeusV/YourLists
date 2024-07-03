package com.heyzeusv.yourlists.util

import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp

/**
 *  Load a string resource with formatting.
 *
 *  @param id The resource identifier.
 *  @param args The format arguments.
 *  @return The string data associated with the resource.
 */
@Composable
@ReadOnlyComposable
fun sRes(@StringRes id: Int, vararg args: Any): String = stringResource(id, *args)

/**
 *  Create a [Painter] from an Android resource id.
 *
 *  @param id Resources object to query the image file from.
 *  @return [Painter] used for drawing the loaded resource.
 */
@Composable
fun pRes(@DrawableRes id: Int): Painter = painterResource(id)

/**
 *  Load a dimension resource.
 *
 *  @param id The resource identifier.
 *  @return The dimension value associated with the resource.
 */
@Composable
@ReadOnlyComposable
fun dRes(@DimenRes id: Int): Dp = dimensionResource(id)