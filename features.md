# Chartastic

This document contains information about what each element of of the bar chart should feature. Features that are checked are currently supported by **Chartastic**

## Bar Chart

 - [x] The chart should be able to represent ranges from positive to negative
 - [x] The chart should be modular and allow for adding and removing elements while adapting seamlessly
 - [ ] The chart should support negative range values
 - [ ] The chart should be able to scrool to reveal more values.
 - [ ] The chart should be modular adaptive. Parst should be added or remove seamlessly
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
* **X Axis Label Top**
* **X Axis label Bottom**
* **Y Axis Label Left**
* **Y Axis Label Right**
* **Major Grid Lines**
* **Minor Grid Lines**
* **Major Tick Lines**
* **Minor Tick Lines**

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
   - [ ] The bars backgrounds should be able to have inner shadow
   - [ ] The bars should be able to suppor different effects
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
   - [x] The bars should be able to have shadows in any directions
   - [x] The bars should be able to have shadows of any color (Glow or shadow)
   - [x] The bars should be able to have rounded corners at any end.
   - [x] The bars should have a controllable rounded corner radius for both ends
   - [x] The bars should be able to have stroke/border of any width and color
   - [ ] The bars should be able to have labels with their respective values
   - [ ] The bars should be able to show tooltips at a desired location upon touch 
   - [ ] The bars should be able to handle long press events
   - [x] The bars should be able to handle hovers events
   - [ ] The bars should be able to listen and adapt to live data changes
   - [x] The bars should be able to have desireable opacity levels
   - [ ] The bars should be able to have decorations set by the user
      * **Possible decorations:**
         * Glow at the top of the bar
         * Drawable at the top, middle or bottom of the bar
         * Othe options desired by the user
   - [ ] The bars should support listen types of listeners
   - [ ] The bars should be able to support ordering. Ex: Highest to lowes, Highest in center, etc
   - [ ] Allow the bars to overlap in any direction (left to right or right to left)
   - [ ] A bar among all the bars should be highlightable with user specified high-lighting.
       * **Possible functionality:**
         * The highlighting should be based on user specified criteria.
   - [ ] The user should be able to specify his/her own bar style 
   - [ ] Each bar should be responsive to changes in neighbor bars
   
   
