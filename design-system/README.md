# Design System Module

This module contains the design system components for the ProExpense app built with Jetpack Compose.

## Features

- **Theme**: Material 3 theme with light and dark mode support
- **Colors**: Custom color palette optimized for expense tracking
- **Typography**: Consistent text styles across the app
- **Components**: Reusable UI components

## Components

### ProExpenseButton
A customizable button component with multiple variants:
- Primary
- Secondary
- Outline

### ProExpenseTextField
A text input component with error handling and validation support.

### ProExpenseCard
A card component for displaying content with consistent styling.

## Usage

To use this module in other modules, add the dependency:

```kotlin
implementation(project(":design-system"))
```

Then import and use the components:

```kotlin
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.design.components.ProExpenseButton
import com.arduia.design.components.ProExpenseTextField

@Composable
fun MyScreen() {
    ProExpenseTheme {
        ProExpenseButton(
            text = "Save",
            onClick = { /* handle click */ }
        )
    }
}
```

## Customization

The design system can be customized by modifying the color schemes, typography, and component styles in their respective files.
