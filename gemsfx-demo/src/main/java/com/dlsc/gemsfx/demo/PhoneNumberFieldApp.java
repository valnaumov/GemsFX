package com.dlsc.gemsfx.demo;

import com.dlsc.gemsfx.PhoneNumberField;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;

import java.util.function.Function;

public class PhoneNumberFieldApp extends Application {

    private static final Function<Object, String> COUNTRY_CODE_CONVERTER = c -> {
        if (c == null) {
            return null;
        }
        PhoneNumberField.CountryCallingCode code = (PhoneNumberField.CountryCallingCode) c;
        return "(+" + code.countryCode() + ") " + code;
    };

    @Override
    public void start(Stage stage) throws Exception {
        PhoneNumberField field = new PhoneNumberField();

        VBox controls = new VBox(10);
        addControl("Available Countries", availableCountriesSelector(field), controls);
        addControl("Preferred Countries", preferredCountriesSelector(field), controls);
        addControl("Default Country", defaultCountrySelector(field), controls);
        addControl("Disable Country", disableCountryCheck(field), controls);
        addControl("Force Local Phone", forceLocalPhoneNumberCheck(field), controls);
        addControl("Strict Mode", strictModeCheck(field), controls);
        addControl("Unmasked", unmaskedModeCheck(field), controls);

        VBox fields = new VBox(10);
        addField(fields, "Country Code", field.countryCallingCodeProperty(), COUNTRY_CODE_CONVERTER);
        addField(fields, "Phone Number", field.phoneNumberProperty());
        addField(fields, "Local Number", field.localPhoneNumberProperty());
        addField(fields, "Formatted Local Number", field.formattedLocalPhoneNumberProperty());
        addField(fields, "Mask", field.maskProperty());

        VBox vBox = new VBox(20);
        vBox.setPadding(new Insets(20));
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(controls, new Separator(), field, new Separator(), fields);

        stage.setTitle("PhoneNumberField");
        stage.setScene(new Scene(vBox, 500, 500));
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();
    }

    private Node availableCountriesSelector(PhoneNumberField field) {
        CheckBox allCountries = new CheckBox("All");

        CheckComboBox<PhoneNumberField.CountryCallingCode> comboBox = new CheckComboBox<>();
        comboBox.getItems().addAll(PhoneNumberField.CountryCallingCode.Defaults.values());
        comboBox.setPrefWidth(250);
        comboBox.getCheckModel().getCheckedItems().addListener((InvalidationListener) observable -> field.getAvailableCountryCodes().setAll(comboBox.getCheckModel().getCheckedItems()));
        comboBox.getCheckModel().checkAll();

        allCountries.selectedProperty().addListener((obs, oldV, newV) -> {
            if (newV) {
                comboBox.getCheckModel().checkAll();
                comboBox.setDisable(true);
            } else {
                comboBox.getCheckModel().clearChecks();
                comboBox.setDisable(false);
            }
        });

        allCountries.setSelected(true);

        HBox box = new HBox(10);
        box.getChildren().addAll(allCountries, comboBox);

        return box;
    }

    private Node preferredCountriesSelector(PhoneNumberField view) {
        CheckComboBox<PhoneNumberField.CountryCallingCode> comboBox = new CheckComboBox<>();
        comboBox.getItems().addAll(PhoneNumberField.CountryCallingCode.Defaults.values());
        comboBox.setPrefWidth(300);
        Bindings.bindContent(view.getPreferredCountryCodes(), comboBox.getCheckModel().getCheckedItems());
        return comboBox;
    }

    private Node defaultCountrySelector(PhoneNumberField view) {
        ComboBox<PhoneNumberField.CountryCallingCode> comboBox = new ComboBox<>();
        comboBox.getItems().add(null);
        comboBox.getItems().addAll(PhoneNumberField.CountryCallingCode.Defaults.values());
        comboBox.valueProperty().bindBidirectional(view.countryCallingCodeProperty());
        return comboBox;
    }

    private Node disableCountryCheck(PhoneNumberField field) {
        CheckBox check = new CheckBox();
        check.selectedProperty().bindBidirectional(field.disableCountryCodeProperty());
        return check;
    }

    private Node forceLocalPhoneNumberCheck(PhoneNumberField field) {
        CheckBox check = new CheckBox();
        check.selectedProperty().bindBidirectional(field.forceLocalNumberProperty());
        return check;
    }

    private Node strictModeCheck(PhoneNumberField field) {
        CheckBox check = new CheckBox();
        check.selectedProperty().bindBidirectional(field.strictModeProperty());
        return check;
    }

    private Node unmaskedModeCheck(PhoneNumberField field) {
        CheckBox check = new CheckBox();
        check.selectedProperty().addListener((obs, oldV, newV) -> {
            if (newV) {
                field.setMaskProvider(null);
            } else {
                field.setMaskProvider(PhoneNumberField.DEFAULT_MASK_PROVIDER);
            }
        });
        return check;
    }

    private void addControl(String name, Node control, VBox controls) {
        Label label = new Label(name);
        label.setPrefWidth(150);
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(label, control);
        HBox.setHgrow(label, Priority.NEVER);
        HBox.setHgrow(control, Priority.ALWAYS);
        controls.getChildren().add(hBox);
    }

    private void addField(VBox fields, String label, ObservableValue property) {
        addField(fields, label, property, null);
    }

    private void addField(VBox fields, String label, ObservableValue property, Function<Object, String> converter) {
        Label value = new Label();
        if (converter == null) {
            value.textProperty().bind(Bindings.convert(property));
        } else {
            value.textProperty().bind(Bindings.createStringBinding(() -> converter.apply(property.getValue()), property));
        }

        Label myLabel = new Label(label + ": ");
        myLabel.setPrefWidth(150);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(myLabel, value);
        HBox.setHgrow(myLabel, Priority.NEVER);
        HBox.setHgrow(value, Priority.ALWAYS);

        fields.getChildren().add(hBox);
    }

    public static void main(String[] args) {
        launch();
    }

}