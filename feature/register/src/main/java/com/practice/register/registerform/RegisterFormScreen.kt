package com.practice.register.registerform

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.practice.designsystem.LightAndDarkPreview
import com.practice.designsystem.components.BlindarTopAppBar
import com.practice.designsystem.components.BottomNextButton
import com.practice.designsystem.components.LabelSmall
import com.practice.designsystem.components.TitleLarge
import com.practice.designsystem.theme.BlindarTheme
import com.practice.register.R
import com.practice.register.RegisterViewModel
import com.practice.util.makeToast

@Composable
fun RegisterFormScreen(
    onBackButtonClick: () -> Unit,
    onNameUpdated: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.registerUiState
    val context = LocalContext.current
    val submitNameFailMessage = stringResource(R.string.submit_name_fail)

    ConstraintLayout(modifier = modifier) {
        val (appBar, nameCard, formNextButton) = createRefs()
        BlindarTopAppBar(
            title = stringResource(id = R.string.register_form_screen),
            onBackButtonClick = onBackButtonClick,
            modifier = Modifier.constrainAs(appBar) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
        RegisterNameCard(
            name = state.name,
            onNameChange = viewModel::onNameChange,
            isValid = state.isNameValid,
            submitName = {
                viewModel.submitName(
                    onSuccess = onNameUpdated,
                    onFail = {
                        context.makeToast(submitNameFailMessage)
                    }
                )
            },
            modifier = Modifier
                .constrainAs(nameCard) {
                    start.linkTo(parent.start)
                    top.linkTo(appBar.bottom)
                    end.linkTo(parent.end)
                    bottom.linkTo(formNextButton.top)
                }
                .fillMaxWidth(0.8f)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
        )
        BottomNextButton(
            text = stringResource(R.string.next_button),
            enabled = true,
            onClick = onNameUpdated,
            modifier = Modifier
                .constrainAs(formNextButton) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .fillMaxWidth()
        )
    }
}

@Composable
private fun RegisterNameCard(
    name: String,
    onNameChange: (String) -> Unit,
    isValid: Boolean,
    submitName: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ConstraintLayout(modifier = modifier) {
        val (title, nameField) = createRefs()
        RegisterNameTitle(
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }
        )
        NameTextField(
            name = name,
            onNameChange = onNameChange,
            isValid = isValid,
            submitName = submitName,
            modifier = Modifier.constrainAs(nameField) {
                top.linkTo(title.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
    }
}

@Composable
private fun RegisterNameTitle(
    modifier: Modifier = Modifier
) {
    TitleLarge(
        text = stringResource(id = R.string.name_title),
        modifier = modifier,
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun NameTextField(
    name: String,
    onNameChange: (String) -> Unit,
    isValid: Boolean,
    submitName: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isError = name.isNotEmpty() && !isValid
    val keyboardController = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        modifier = modifier,
        isError = isError,
        placeholder = {
            LabelSmall(text = stringResource(id = R.string.name_placeholder))
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
        keyboardActions = KeyboardActions(
            onSend = {
                keyboardController?.hide()
                submitName()
            }
        ),
        maxLines = 1,
    )
}

@LightAndDarkPreview
@Composable
private fun RegisterNameCardPreview() {
    var name by remember { mutableStateOf("") }
    val isNameValid by remember { derivedStateOf { NameValidator.validate(name) } }
    BlindarTheme {
        RegisterNameCard(
            name = name,
            onNameChange = { name = it },
            isValid = isNameValid,
            submitName = {},
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
        )
    }
}

@LightAndDarkPreview
@Composable
private fun RegisterFormScreenPreview() {
    BlindarTheme {
        RegisterFormScreen(
            onBackButtonClick = {},
            onNameUpdated = { },
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary),
        )
    }
}