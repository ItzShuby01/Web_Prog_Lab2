"use strict";

const form = document.getElementById("data-form");
const yParamInput = document.getElementById("y-param-input");
const errorDisplay = document.getElementById("error");
const currentTimeSpan = document.getElementById("curr-time");

const canvas = document.getElementById('graph');
const ctx = canvas.getContext('2d');
const canvasWidth = canvas.width;
const canvasHeight = canvas.height;

// Select the actual radio groups
const rRadioGroup = document.getElementById("r-radio-group");
const xRadioGroup = document.getElementById("x-radio-group");

// Function to get the value of the currently checked radio button in a group
const getRadioValue = (name) => {
    const radio = document.querySelector(`input[name="${name}"]:checked`);
    return radio ? radio.value : null;
};

// Initialize state and points using data passed from the JSP (window.initialR is set based on the CHECKED radio button)
let state = {
    // Read initial values from the currently checked radio buttons or fall back
    x: parseFloat(getRadioValue('x')) || 0.0,
    y: parseFloat(yParamInput.value) || 0.0,
    r: window.initialR || 2.0 // Read from the value set in JSP script block
};
let points = window.initialPoints || [];


//--- CANVAS ---
//Used a fixed reference of 5 units = half the canvas width/height (200px for 400x400)
const referenceUnit = canvasWidth / 10;
const toCanvasX = (x) => canvasWidth / 2 + x * referenceUnit;
const toCanvasY = (y) => canvasHeight / 2 - y * referenceUnit;

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


//--- INITIAL SETUP AND TIME ---
function updateCurrentTime() {
    currentTimeSpan.textContent = new Date().toLocaleString();
}
setInterval(updateCurrentTime, 1000);
updateCurrentTime();


//--- VALIDATION RULES ---
const validationRules = {
    // R is a standard radio input
    r: {
        isValid: (val) => {
            const num = parseFloat(val);
            const allowedRValues = [1, 2, 3, 4, 5];
            if (isNaN(num) || !allowedRValues.includes(num)) {
                return `R value must be one of: ${allowedRValues.join(", ")}.`;
            }
            return true;
        },
        updateState: (val) => {
            state.r = parseFloat(val);
        }
    },
    // X is a standard radio input
    x: {
        isValid: (val) => {
            const num = parseFloat(val);
            const allowedXValues = [-3, -2, -1, 0, 1, 2, 3, 4, 5];
            if (isNaN(num) || !allowedXValues.includes(num)) {
                return "X must be one of the selected radio button values.";
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

    const validationResult = rule.isValid(value);

    // Y   visible hint element in the immediate vicinity
    if (inputName === 'y') {
        const hint = rule.hint;
        hint.classList.remove("error");
        hint.textContent = validationRules.y.input.getAttribute("data-original-hint");
        hint.style.visibility = "visible";

        if (validationResult === true) {
            rule.input.classList.remove("invalid");
            return true;
        } else {
            hint.textContent = validationResult;
            hint.classList.add("error");
            rule.input.classList.add("invalid");
            return false;
        }
    } else {
        // For R and X, error handling relies on form submit for radio buttons
        return validationResult === true;
    }
}

// Initial Y setup
yParamInput.setAttribute("data-original-hint", yParamInput.nextElementSibling.textContent);
validateInput("y", yParamInput.value);
validationRules.y.updateState(yParamInput.value);


//--- EVENT LISTENERS ---

// R Radio Group Listener: Only updates the graph when R changes
rRadioGroup.addEventListener("change", () => {
    errorDisplay.classList.remove('visible'); // Hide general error on interaction
    const rValue = getRadioValue('r');
    if (rValue && validateInput("r", rValue)) {
        validationRules.r.updateState(rValue);
        drawGraph(state.r);
    }
});

// X Radio Group Listener
xRadioGroup.addEventListener("change", () => {
    errorDisplay.classList.remove('visible'); // Hide general error on interaction
});

// Y Input Listener
yParamInput.addEventListener("input", (event) => {
    errorDisplay.classList.remove('visible'); // Hide general error on interaction
    validateInput("y", event.target.value);
    validationRules.y.updateState(event.target.value);
});


//--- FORM SUBMISSION ---
form.addEventListener("submit", function (ev) {
    // Get current radio values from the form before submission
    const rValue = getRadioValue('r');
    const xValue = getRadioValue('x');
    const yValue = yParamInput.value;

    // Client-side validation for all fields
    const isRValid = validateInput("r", rValue);
    const isXValid = validateInput("x", xValue);
    const isYValid = validateInput("y", yValue);

    if (!isRValid || !isXValid || !isYValid) {
        ev.preventDefault();
        // Display a more specific error message based on which fields failed
        let errorMsg = "Please correct the following errors: ";
        if (!isRValid) errorMsg += " [R must be selected]";
        if (!isXValid) errorMsg += " [X must be selected]";
        if (!isYValid) errorMsg += " [Y is invalid]";

        errorDisplay.textContent = errorMsg.replace(': [', ': ').replace('[', ' and ').replace(']', '');
        errorDisplay.classList.add('visible');
        return;
    }
});


//--- GRAPH CLICK FUNCTIONALITY ---
canvas.addEventListener('click', function(event) {
    errorDisplay.classList.remove('visible');

    // Get current R value from the checked radio button
    const rValue = getRadioValue('r');
    const isRValid = validationRules.r.isValid(rValue) === true;

    if (!isRValid) {
        errorDisplay.textContent = "Please select a valid R value (1-5) before clicking the graph.";
        errorDisplay.classList.add('visible');
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

    // Check if clicked point is within canvas bounds [-5, 5]
    if (xCoord < -5 || xCoord > 5 || yCoord < -5 || yCoord > 5) {
        errorDisplay.textContent = `Clicked point (${xCoord.toFixed(2)}, ${yCoord.toFixed(2)}) is outside the acceptable canvas range of -5 to 5.`;
        errorDisplay.classList.add('visible');
        return;
    }

    // Round coordinates for clean submission
    const roundedX = xCoord.toFixed(8);
    const roundedY = yCoord.toFixed(8);

    // Submit to server via GET request
    window.location.href = `controller?x=${roundedX}&y=${roundedY}&r=${rNum}`;
});


//--- INITIAL DRAW ---
window.onload = () => {
    drawGraph(state.r);


    //NB: No need to manually highlight R button; the browser handles native radio button selection.
};