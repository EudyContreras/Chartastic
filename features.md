# Chartastic

This document contains information about what each element of of the bar chart should feature. Features that are checked are currently supported by **Chartastic**

## General Requirements

* Allow the chart to have empty state for when there is no data to be shown.
* Allow the user to specify rules for how the chart will be built. 
* The chart should have a loading state during data fetch or process with progress indication.
* Allow the user to specify rules for how much data is shown based on some criteria.
* Allow the user to specify min and max sizes for the chart.
* Allow the user to specify visual rendering rules for different screen orientations.
* Each chart element should be able to handle all sorts of touch and gesture events.
* Allow the user to specify transitions between the states of the chart.
* The chart should be able to visually interpolate between different datasets with timed animations.
* The chart plot are should allow scroll, zoom and panning.
* The user should be able to bind to charts that share a dataset
* The chart should support different types of axis values.
* The chart plot are should support different chart types stacked on top of eachother.
* The chart should support custom tooltip layout.
* The chart should support custom element layouts.
* The chart view should be able to reveal onto the screen using user specified animations.

## Personal Todo List

* Add ticks logic ticks coupled with bar clusters
* Add posiblility of user to add formatting for the labels
* Improve existing APIs so that code becomes more easily maintainable

## Bar Chart

 - [x] The chart should be able to represent ranges from positive to negative
 - [x] The chart should be modular and allow for adding and removing elements while adapting seamlessly
 - [ ] The chart should support negative range values
 - [ ] The chart should be able to scrool to reveal more values.
 - [ ] The chart should be modular adaptive. Parst should be added or remove seamlessly
 - [ ] The chart should be able to input a max and min positive range for any axis
 - [ ] The chart should be able to input a max and min negative range for any axis
 
## Bar Chart Elements

* **Bar Series**
* **Bar Series Dividers**
* **Bar Cluster**
* **Bar Cluster Dividers**
* **Bar Tooltip**
* **Bar Tooltip Connector**
* **Bar Background**
* **Bar Decoration**
* **Data Point Marker**
* **Data Point Label**
* **Trend Line**
* **Trend Line Point**
* **Legend Area Top**
* **Legend Area Top**
* **Legend Area**
* **Legend Element**
* **Plot Area**
* **Plot Area Border**
* **Plot Area Scroller**
* **Value Interceptor**
* **Value Interceptor Tooltip**
* **X Axis**
* **Y Axis**
* **Major Grid Lines**
* **Minor Grid Lines**
* **Major Tick Lines**
* **Minor Tick Lines**

## Bar Chart Bugs & Issues

**Problem:**
* If the the point count is close to the record count it can result on repeated values.

**Possible fixes:**
1. Clamp the point count to a number that does not produce repeated values
2. Decrease the rounding scale of the values.
3. Get rid of the rounding when creating the values

Here is a list of all the element supported by the bar chart and their current status within development.

* **Bars or Series**

   - [x] The bars should be able to process touch events
   - [x] The bars should have a desired touch feedback color changes
   - [ ] The bars should be able to animate upon touch
   - [x] The bars should be able to have reveal and conceal animations
     * **Possible animations**
        * The animatioss should be able to have any interpolation
        * The animations should be able to have stagger
        * The animations should be able to take type ex: (Left to right), (Center to edge)
        * The animations should be able to notify upon ending
   - [x] The bars should be able to have backgrounds of any color
   - [x] The bars should be able to have backgrounds with desired padding
   - [x] The bars backgrounds should be able to have inner shadow
   - [ ] The bars should be able to support different effects
     * **Possible effects**
        * Ghosting: The bars have ghosted bars under
        * The bars have animated glow around them
   - [x] The bars reveal/conceal should be able to be binded to scrolling
     * **Possible functionality**
        * The scroll bind should be controlled base on how much of the chart is visible
        * The user should be able to controll when the bar is reveal based on scrolling
   - [x] The bars should be able to have any desired color
   - [x] The bars should be able to have gradient shaders with any colors or direction
   - [x] The bars should be able to interpolate color accross themselves
   - [x] The bars should be be able to able to have elevation produced shadows
   - [x] The bars should be able to have outer and inner shadows in any directions
   - [x] The bars should be able to have outer and inner shadows of any color (Glow or shadow)
   - [x] The bars should be able to have rounded corners at any end.
   - [x] The bars should have a controllable rounded corner radius for both ends
   - [x] The bars should be able to have stroke/border of any width and color
   - [ ] The bars should be able to have labels with their respective values
   - [x] The bars should be able to show tooltips at a desired location upon touch 
   - [x] The bars should be able to handle long press events
   - [x] The bars should be able to handle hovers events
   - [ ] The bars should be able to listen and adapt to live data changes
   - [x] The bars should be able to have desireable opacity levels
   - [ ] The bars should be able to have decorations set by the user
      * **Possible decorations:**
         * Glow at the top of the bar
         * Drawable at the top, middle or bottom of the bar
         * Othe options desired by the user
   - [ ] The bars should support different types of listeners (Bar revealed, Bar selected, Bar hovererd, etc)
   - [x] The bars should be able to support ordering. Ex: Highest to lowes, Highest in center, etc
   - [ ] Allow the bars to overlap in any direction (left to right or right to left)
   - [ ] A bar among all the bars should be highlightable with user specified high-lighting.
       * **Possible functionality:**
         * The highlighting should be based on user specified criteria.
   - [x] The user should be able to specify his/her own bar style 
   - [ ] Each bar should be responsive to changes in neighbor bars
   - [ ] The bars should be able to be segmented with desired segmentation spacing and shape (Circle, Rect)
   - [ ] The bars should be able to be grouped or clusterd by a desired key value
   - [ ] The bar cluster should support parallel and sequential group animations
   
* **Ploat Area**

   - [ ] The plot area should be able to scrool to reveal more series/bars
   
* **Y Axis**

   - [x] The y axis should be able to show a desired number of value/label points
   - [x] The y axis should be able to feature any desired font of any style, size and color
   - [x] The y axis module visiblility should be able to be toggled
   - [x] The y axis labels should support left and right paddings
   - [x] The y axis labels should support ticks of any desired color, thickness and lenght
   - [x] The y axis labels should be able to support any type of value (integers, doubles, etc)
   - [x] The y axis labels should support preffix and suffix appenditures to the values
   - [x] The y axis labels should be properly rounded based on magnitude
   - [x] The y axis labels should minor ticks
   - [ ] The y axis labels should be able to be angled/skewed
   - [ ] The y axis label values should support different default and user createdd formats
   - [ ] The y axis should support user submitted values while sticking to the real values of the data
   
 
