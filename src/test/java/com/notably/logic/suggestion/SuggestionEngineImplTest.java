package com.notably.logic.suggestion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.notably.commons.path.AbsolutePath;
import com.notably.commons.path.exceptions.InvalidPathException;
import com.notably.model.Model;
import com.notably.model.ModelManager;
import com.notably.model.block.Block;
import com.notably.model.block.BlockImpl;
import com.notably.model.block.BlockModel;
import com.notably.model.block.BlockModelImpl;
import com.notably.model.block.Title;
import com.notably.model.suggestion.SuggestionItem;
import com.notably.model.suggestion.SuggestionItemImpl;
import com.notably.model.suggestion.SuggestionModel;
import com.notably.model.suggestion.SuggestionModelImpl;
import com.notably.model.viewstate.ViewStateModel;
import com.notably.model.viewstate.ViewStateModelImpl;

public class SuggestionEngineImplTest {
    private static AbsolutePath toRoot;
    private static AbsolutePath toCs2103;
    private static AbsolutePath toCs3230;
    private static AbsolutePath toCs2103Week1;
    private static AbsolutePath toCs2103Week2;
    private static AbsolutePath toCs2103Week3;
    private static AbsolutePath toCs2103Week1Lecture;
    private static Model model;
    private static SuggestionEngine suggestionEngine;

    @BeforeAll
    public static void setUp() throws InvalidPathException {
        // Set up paths
        toRoot = AbsolutePath.fromString("/");
        toCs2103 = AbsolutePath.fromString("/CS2103");
        toCs3230 = AbsolutePath.fromString("/CS3230");
        toCs2103Week1 = AbsolutePath.fromString("/CS2103/Week1");
        toCs2103Week2 = AbsolutePath.fromString("/CS2103/Week2");
        toCs2103Week3 = AbsolutePath.fromString("/CS2103/Week3");
        toCs2103Week1Lecture = AbsolutePath.fromString("/CS2103/Week1/Lecture");

        // Set up model
        BlockModel blockModel = new BlockModelImpl();
        SuggestionModel suggestionModel = new SuggestionModelImpl();
        ViewStateModel viewStateModel = new ViewStateModelImpl();
        model = new ModelManager(blockModel, suggestionModel, viewStateModel);

        // Add test data to model
        Block cs2103 = new BlockImpl(new Title("CS2103"));
        Block cs3230 = new BlockImpl(new Title("CS3230"));
        model.addBlockToCurrentPath(cs2103);
        model.addBlockToCurrentPath(cs3230);

        Block week1 = new BlockImpl(new Title("Week1"));
        Block week2 = new BlockImpl(new Title("Week2"));
        Block week3 = new BlockImpl(new Title("Week3"));
        model.setCurrentlyOpenBlock(toCs2103);
        model.addBlockToCurrentPath(week1);
        model.addBlockToCurrentPath(week2);
        model.addBlockToCurrentPath(week3);

        Block lecture = new BlockImpl(new Title("Lecture"));
        model.setCurrentlyOpenBlock(toCs2103Week1);
        model.addBlockToCurrentPath(lecture);

        // Set up SuggestionEngine
        suggestionEngine = new SuggestionEngineImpl(model);
    }

    @Test
    public void suggest_inputLengthTooShort_suggestionsAndResponseTextCleared() {
        model.setInput("");
        assertTrue(model.getSuggestions().isEmpty());
        assertTrue(model.responseTextProperty().getValue().isEmpty());

        model.setInput("o");
        assertTrue(model.getSuggestions().isEmpty());
        assertTrue(model.responseTextProperty().getValue().isEmpty());
    }

    @Test
    public void suggest_correctDeleteCommandValidArgs_returnsDeleteSuggestionCommand() {
        String title = "Lecture";
        model.setInput("delete -t " + title);

        // Test response text
        String expectedResponseText = "Delete a note titled \"" + title + "\"";
        assertEquals(Optional.of(expectedResponseText), model.responseTextProperty().getValue());

        // Expected result
        List<SuggestionItem> expectedSuggestions = new ArrayList<>();
        SuggestionItem cs2103Week1Lecture = new SuggestionItemImpl(toCs2103Week1Lecture.getStringRepresentation(),
                null);
        expectedSuggestions.add(cs2103Week1Lecture);

        List<SuggestionItem> suggestions = model.getSuggestions();

        for (int i = 0; i < expectedSuggestions.size(); i++) {
            SuggestionItem suggestion = suggestions.get(i);
            SuggestionItem expectedSuggestion = expectedSuggestions.get(i);
            assertEquals(expectedSuggestion.getProperty("displayText"), suggestion.getProperty("displayText"));
        }

        List<String> expectedInputs = new ArrayList<>();
        expectedInputs.add("delete -t " + toCs2103Week1Lecture.getStringRepresentation());

        for (int i = 0; i < expectedInputs.size(); i++) {
            SuggestionItem suggestionItem = suggestions.get(i);
            String expectedInput = expectedInputs.get(i);
            suggestionItem.getAction().run();
            String input = model.getInput();
            assertEquals(expectedInput, input);
        }

    }

    @Test
    public void suggest_correctDeleteCommandInvalidArgs_returnsErrorSuggestionCommand() {
        String title = "CS2222";
        model.setInput("delete " + title);

        // Expected result
        String expectedResponseText = "Delete a note titled \"" + title + "\"";
        assertEquals(Optional.of(expectedResponseText), model.responseTextProperty().getValue());
    }

    @Test
    public void suggest_correctedDeleteCommandValidArgs_returnsDeleteSuggestionCommand() {
        String title = "Lecture";
        model.setInput("dele -t " + title);

        // Test response text
        String expectedResponseText = "Delete a note titled \"" + title + "\"";
        assertEquals(Optional.of(expectedResponseText), model.responseTextProperty().getValue());

        // Expected result
        List<SuggestionItem> expectedSuggestions = new ArrayList<>();
        SuggestionItem cs2103Week1Lecture = new SuggestionItemImpl(toCs2103Week1Lecture.getStringRepresentation(),
                null);
        expectedSuggestions.add(cs2103Week1Lecture);

        List<SuggestionItem> suggestions = model.getSuggestions();

        for (int i = 0; i < expectedSuggestions.size(); i++) {
            SuggestionItem suggestion = suggestions.get(i);
            SuggestionItem expectedSuggestion = expectedSuggestions.get(i);
            assertEquals(expectedSuggestion.getProperty("displayText"), suggestion.getProperty("displayText"));
        }

        List<String> expectedInputs = new ArrayList<>();
        expectedInputs.add("dele -t " + toCs2103Week1Lecture.getStringRepresentation());

        for (int i = 0; i < expectedInputs.size(); i++) {
            SuggestionItem suggestionItem = suggestions.get(i);
            String expectedInput = expectedInputs.get(i);
            suggestionItem.getAction().run();
            String input = model.getInput();
            assertEquals(expectedInput, input);
        }

    }

    @Test
    public void suggest_correctedDeleteCommandInvalidArgs_returnsErrorSuggestionCommand() {
        String title = "CS2222";
        model.setInput("dele -t " + title);

        // Expected result
        String expectedResponseText = "Delete a note titled \"" + title + "\"";
        assertEquals(Optional.of(expectedResponseText), model.responseTextProperty().getValue());
    }

    @Test
    public void suggest_correctedOpenCommandValidArgs_returnsOpenSuggestionCommand() {
        String title = "Lecture";
        model.setInput("oen -t " + title);

        // Test response text
        String expectedResponseText = "Open a note titled \"" + title + "\"";
        assertEquals(Optional.of(expectedResponseText), model.responseTextProperty().getValue());

        // Expected result
        List<SuggestionItem> expectedSuggestions = new ArrayList<>();
        SuggestionItem cs2103Week1Lecture = new SuggestionItemImpl(toCs2103Week1Lecture.getStringRepresentation(),
                null);
        expectedSuggestions.add(cs2103Week1Lecture);

        List<SuggestionItem> suggestions = model.getSuggestions();

        for (int i = 0; i < expectedSuggestions.size(); i++) {
            SuggestionItem suggestion = suggestions.get(i);
            SuggestionItem expectedSuggestion = expectedSuggestions.get(i);
            assertEquals(expectedSuggestion.getProperty("displayText"), suggestion.getProperty("displayText"));
        }

        List<String> expectedInputs = new ArrayList<>();
        expectedInputs.add("oen -t " + toCs2103Week1Lecture.getStringRepresentation());

        for (int i = 0; i < expectedInputs.size(); i++) {
            SuggestionItem suggestionItem = suggestions.get(i);
            String expectedInput = expectedInputs.get(i);
            suggestionItem.getAction().run();
            String input = model.getInput();
            assertEquals(expectedInput, input);
        }

    }

    @Test
    public void suggest_correctedOpenCommandInvalidArgs_returnsErrorSuggestionCommand() {
        String title = "CS2222";
        model.setInput("opn -t " + title);

        // Expected result
        String expectedResponseText = "Open a note titled \"" + title + "\"";
        assertEquals(Optional.of(expectedResponseText), model.responseTextProperty().getValue());
    }

    @Test
    public void suggest_correctedNewCommand_returnsNewSuggestionCommand() {
        String responseText = "Create a new note titled \"%s\".";
        String title = "NewNote";
        model.setInput("nw -t " + title);

        // Expected result
        String expectedResponseText = String.format(responseText, title);
        assertEquals(Optional.of(expectedResponseText), model.responseTextProperty().getValue());
    }

    @Test
    public void suggest_correctedEditCommand_returnsEditSuggestionCommand() {
        model.setInput("edt -t NewNote -b lorem ipsum");

        // Expected result
        String expectedResponseText = "Edit this note";
        assertEquals(Optional.of(expectedResponseText), model.responseTextProperty().getValue());
    }

    @Test
    public void suggest_correctedHelpCommand_returnsHelpSuggestionCommand() {
        model.setInput("hAlp");

        // Expected result
        String expectedResponseText = "Display a list of available commands";
        assertEquals(Optional.of(expectedResponseText), model.responseTextProperty().getValue());
    }

    @Test
    public void suggest_correctedExitCommand_returnsExitSuggestionCommand() {
        model.setInput("ex");

        // Expected result
        String expectedResponseText = "Exit the application";
        assertEquals(Optional.of(expectedResponseText), model.responseTextProperty().getValue());
    }
}