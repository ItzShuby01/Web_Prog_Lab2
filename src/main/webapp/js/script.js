"use strict";

const form = document.getElementById("data-form");
const rParamInput = document.getElementById("r-param-input");
const xParamInput = document.getElementById("x-param-input");
const yParamInput = document.getElementById("y-param-input");
const errorDisplay = document.getElementById("error");
const clearResButton = document.getElementById("clear-results");
const currentTimeSpan = document.getElementById("curr-time");

const canvas = document.getElementById('graph');
const ctx = canvas.getContext('2d');
const canvasWidth = canvas.width;
const canvasHeight = canvas.height;

const rButtonGroup = document.getElementById("r-button-group");
const xButtonGroup = document.getElementById("x-button-group");


// Initialize state and points using data passed from the JSP (if available)
let state = {
    // Read initial values from hidden inputs
    x: parseFloat(xParamInput.value) || 0,
    y: 0,
    r: parseFloat(rParamInput.value) || 2.0
};
let points = window.initialPoints || [];



//Used a fixed reference of 5 units = half the canvas width/height (200px for 400x400)
const referenceUnit = canvasWidth / 10;
const toCanvasX = (x) => canvasWidth / 2 + x * referenceUnit;
const toCanvasY = (y) => canvasHeight / 2 - y * referenceUnit;

// Helper to manage button selection classes.
function highlightButton(group, className, selectedButton) {
    // Remove the selection class from ALL buttons in the group
    group.querySelectorAll(`.${className}`).forEach(btn => btn.classList.remove(className));
    // Apply the selection class to the single selected button
    if (selectedButton) {
        selectedButton.classList.add(className);
    }
}



// Draws a single point on the canvas.
function drawPoint(x, y, hit) {
    const px = toCanvasX(x);
    const py = toCanvasY(y);

    ctx.strokeStyle = hit ? "lime" : "red";
    ctx.lineWidth = 2;

    ctx.beginPath();
    // Draw an 'X' to mark the point
    ctx.moveTo(px - 4, py - 4);
    ctx.lineTo(px + 4, py + 4);
    ctx.moveTo(px - 4, py + 4);
    ctx.lineTo(px + 4, py - 4);
    ctx.stroke();
}


//Draws the required graph shape (Q2 Triangle, Q3 Quarter Circle, Q4 Rectangle)
function drawGraph(r) {
    ctx.clearRect(0, 0, canvasWidth, canvasHeight);

    // Center point
    const centerX = canvasWidth / 2;
    const centerY = canvasHeight / 2;

    // Pixel lengths for R and R/2
    const rHalfPx = (r / 2) * referenceUnit;

    ctx.fillStyle = "rgba(51, 153, 255, 0.5)";
    ctx.strokeStyle = "#3399FF";
    ctx.beginPath();

    // Start at (0, 0)
    ctx.moveTo(centerX, centerY);

    // 1. Quadrant IV: Rectangle (0, 0) to (R, -R/2)
    ctx.lineTo(toCanvasX(r), centerY); // To (R, 0)
    ctx.lineTo(toCanvasX(r), toCanvasY(-r / 2)); // To (R, -R/2)
    ctx.lineTo(centerX, toCanvasY(-r / 2)); // To (0, -R/2)

    // 2. Quadrant III: Quarter Circle (Radius R/2)
    // Arc from (0, -R/2) to (-R/2, 0)
    // Center X, Center Y, Radius, Start Angle (rad), End Angle (rad), Counter-clockwise
    ctx.arc(centerX, centerY, rHalfPx, Math.PI / 2, Math.PI, false);

    // 3. Quadrant II : Triangle (Vertices: (-R/2, 0), (0, 0), (0, R))
    //Currently at (-R/2, 0) after the arc.
    // Line to (0, 0)
    ctx.lineTo(centerX, centerY);

    // Line up to (0, R)
    ctx.lineTo(centerX, toCanvasY(r));
    // Line down to (-R/2, 0) and close path
    ctx.lineTo(toCanvasX(-r / 2), centerY);
    ctx.closePath();
    ctx.fill();
    ctx.stroke();


    // AXIS & GRID
    ctx.strokeStyle = '#373737';
    ctx.lineWidth = 1;

    ctx.beginPath();
    ctx.moveTo(0, centerY); ctx.lineTo(canvasWidth, centerY); // X-axis
    ctx.moveTo(centerX, 0); ctx.lineTo(centerX, canvasHeight); // Y-axis
    ctx.stroke();

    // Arrows and labels
    ctx.beginPath();
    ctx.moveTo(canvasWidth - 5, centerY - 3); ctx.lineTo(canvasWidth, centerY); ctx.lineTo(canvasWidth - 5, centerY + 3);
    ctx.moveTo(centerX - 3, 5); ctx.lineTo(centerX, 0); ctx.lineTo(centerX + 3, 5);
    ctx.stroke();

    // Draw grid marks and labels (for the fixed -5 to 5 grid)
    ctx.font = '12px Roboto';
    ctx.textAlign = 'center';
    const maxCoord = 5;

    for (let i = -maxCoord; i <= maxCoord; i += 1) {
        if (i === 0) continue;

        const xPx = toCanvasX(i);
        const yPx = toCanvasY(i);

        // Grid lines (light gray)
        ctx.strokeStyle = 'rgba(102, 102, 102, 0.5)';
        ctx.lineWidth = 0.5;

        ctx.beginPath();
        // X-axis ticks
        ctx.moveTo(xPx, centerY - 3);
        ctx.lineTo(xPx, centerY + 3);
        // Y-axis ticks
        ctx.moveTo(centerX - 3, yPx);
        ctx.lineTo(centerX + 3, yPx);
        ctx.stroke();

        // Labels (white text)
        ctx.fillStyle = '#FFFFFF';
        ctx.fillText(i.toString(), xPx, centerY + 18);
        ctx.fillText(i.toString(), centerX - 10, yPx + 5);
    }

    // Draw axis pointers
    ctx.fillText('X', canvasWidth - 15, centerY + 18);
    ctx.fillText('Y', centerX - 10, 15);
    ctx.fillText('0', centerX - 10, centerY + 18);

    // Redraw points from the session
    points.forEach(p => drawPoint(p.x, p.y, p.hit));
}


//INITIAL SETUP AND TIME
function updateCurrentTime() {
    currentTimeSpan.textContent = new Date().toLocaleString();
}
setInterval(updateCurrentTime, 1000);
updateCurrentTime();

// VALIDATION RULES
const validationRules = {
    r: {
        input: rParamInput,
        hint: rParamInput.nextElementSibling,
        isValid: (val) => {
            const num = parseFloat(val);
            const allowedRValues = [1, 2, 3, 4, 5];
            if (!allowedRValues.includes(num)) {
                return `Value must be one of: ${allowedRValues.join(", ")}.`;
            }
            return true;
        },
        updateState: (val) => {
            state.r = parseFloat(val);
        }
    },
    x: {
        input: xParamInput,
        hint: document.querySelector('.x-coord-label')?.nextElementSibling || errorDisplay,
        // Only check the universal bounds [-3, 5], allowing floats from graph clicks
        isValid: (val) => {
            const num = parseFloat(val);
            // X value must be a valid number between -3 and 5
            if (isNaN(num) || num < -3 || num > 5) {
                return "X value is out of bounds (-3 to 5).";
            }
            return true;
        },
        updateState: (val) => {
            state.x = parseFloat(val);
        }
    },
    y: {
        input: yParamInput,
        hint: yParamInput.nextElementSibling,
        isValid: (val) => {
            const normalizedVal = val.replace(',', '.');
            if (!/^-?\d+(\.\d+)?$/.test(normalizedVal)) {
                return "Value is not a valid number.";
            }
            const num = parseFloat(normalizedVal);
            // Y value must be a valid number between -3 and 5, inclusive.
            if (isNaN(num) || num < -3 || num > 5) {
                return "Value must be between -3 and 5.";
            }
            return true;
        },
        updateState: (val) => {
            state.y = parseFloat(val);
        }
    }
};

// Executes validation and updates UI for hints/errors.
function validateInput(inputName, value) {
    const rule = validationRules[inputName];
    const hint = rule.hint;

    const validationResult = rule.isValid(value);

    // Hide all hints initially for cleaner display
    if (hint) hint.style.visibility = "hidden";

    if (validationResult === true) {
        rule.input.classList.remove("invalid");
        return true;
    } else {
        if (hint) {
            hint.textContent = validationResult;
            hint.style.visibility = "visible";
            hint.classList.add("error");
        }
        rule.input.classList.add("invalid");
        return false;
    }
}

// Initial state and validation
Object.keys(validationRules).forEach(key => {
    const rule = validationRules[key];
    rule.updateState(rule.input.value);
    // Ensure hidden inputs start without invalid class
    if (key === 'r' || key === 'x') {
        rule.input.classList.remove("invalid");
    }
});


//EVENT LISTENERS

// R Button Group Listener
rButtonGroup.addEventListener("click", (event) => {
    const button = event.target;
    if (button.classList.contains("r-button")) {
        highlightButton(rButtonGroup, 'selected-r', button);

        const rValue = button.getAttribute('data-r');
        rParamInput.value = rValue;

        validateInput("r", rValue);
        validationRules.r.updateState(rValue);
        drawGraph(state.r);
    }
});

// X Button Group Listener
if (xButtonGroup) {
    xButtonGroup.addEventListener("click", (event) => {
        const button = event.target;
        if (button.classList.contains("x-button")) {
            highlightButton(xButtonGroup, 'selected-x', button);

            const xValue = button.getAttribute('data-x');
            xParamInput.value = xValue; // Update the hidden input

            validateInput("x", xValue);
            validationRules.x.updateState(xValue);
            // Re-validate Y, as Y is often submitted with X
            validateInput("y", yParamInput.value);
        }
    });
}

// Y Input Listener
yParamInput.addEventListener("input", (event) => {
    validateInput("y", event.target.value);
    validationRules.y.updateState(event.target.value);
});


// FORM SUBMISSION
form.addEventListener("submit", function (ev) {
    // Client-side validation for all fields
    const isRValid = validateInput("r", rParamInput.value);
    const isXValid = validateInput("x", xParamInput.value);
    const isYValid = validateInput("y", yParamInput.value);

    if (!isRValid || !isXValid || !isYValid) {
        ev.preventDefault();
        errorDisplay.textContent = "Please correct the input errors and ensure X/R are selected.";
        errorDisplay.hidden = false;
        return;
    }
});


//GRAPH CLICK FUNCTIONALITY
canvas.addEventListener('click', function(event) {
    errorDisplay.hidden = true;

    // Check if R is set and valid
    const rValue = rParamInput.value;
    const isRValid = validationRules.r.isValid(rValue) === true;

    if (!isRValid) {
        errorDisplay.textContent = "Please select a valid R value (1-5) before clicking the graph.";
        errorDisplay.hidden = false;
        return;
    }
    const rNum = parseFloat(rValue);

    // Determine clicked coordinates
    const rect = canvas.getBoundingClientRect();
    const clickX = event.clientX - rect.left;
    const clickY = event.clientY - rect.top;

    // Convert pixel coordinates to graph coordinates
    const centerX = canvasWidth / 2;
    const centerY = canvasHeight / 2;

    const xCoord = (clickX - centerX) / referenceUnit;
    const yCoord = (centerY - clickY) / referenceUnit;

    // Round coordinates to 8 decimal places for clean submission
    const roundedX = xCoord.toFixed(8);
    const roundedY = yCoord.toFixed(8);

    // Client-side validation on the clicked X and Y values
    const isXClickValid = validationRules.x.isValid(roundedX) === true;
    const isYClickValid = validationRules.y.isValid(roundedY) === true;

    if (!isXClickValid) {
        errorDisplay.textContent = `X coordinate (${roundedX}) is outside the acceptable range of -3 to 5.`;
        errorDisplay.hidden = false;
        return;
    }
    if (!isYClickValid) {
        errorDisplay.textContent = `Y coordinate (${roundedY}) is outside the acceptable range of -3 to 5.`;
        errorDisplay.hidden = false;
        return;
    }

    // Submit to server via GET request
    window.location.href = `controller?x=${roundedX}&y=${roundedY}&r=${rNum}`;
});


// Draw graph and highlight initial selections
window.onload = () => {
    drawGraph(state.r);

    // Highlight the currently selected R button
    document.querySelector(`.r-button[data-r="${state.r.toFixed(1)}"]`)?.classList.add('selected-r');
    // Highlight the currently selected X button
    document.querySelector(`.x-button[data-x="${state.x.toFixed(1)}"]`)?.classList.add('selected-x');
};
