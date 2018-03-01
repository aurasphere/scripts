/*
 * MIT License
 *
 * Copyright (c) 2017 Donato Rimenti
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
 
/* Gets the elements whose animations needs to be changed. */
var bar = document.getElementById("bluebarRoot").firstChild;
var searchbox = bar.firstChild.firstChild.firstChild.children[1].firstChild;

/* Adds the animation style to the page. */
var styleElement = document.createElement('style');
styleElement.innerHTML = "\
@keyframes colorchange {\
      0%   {background: #9400D3; border: #9400D3;}\
      14%  {background: #4B0082; border: #4B0082;}\
      28%  {background: #0000FF; border: #0000FF;}\
      42%  {background: #00FF00; border: #00FF00;}\
      56%  {background: #FFFF00; border: #FFFF00;}\
	  70%  {background: #FF7F00; border: #FF7F00;}\
	  84%  {background: #FF0000; border: #FF0000;}\
	  100% {background: #9400D3; border: #9400D3;}\
}\
\
@-webkit-keyframes colorchangeBorderOnly {\
      0%   {border: #9400D3;}\
      14%  {border: #4B0082;}\
      28%  {border: #0000FF;}\
      42%  {border: #00FF00;}\
      56%  {border: #FFFF00;}\
	  70%  {border: #FF7F00;}\
	  84%  {border: #FF0000;}\
	  100% {border: #9400D3;}\
}\
\
/* Safari and Chrome - necessary duplicates */\
@-webkit-keyframes colorchange {\
      0%   {background: #9400D3; border: #9400D3;}\
      14%  {background: #4B0082; border: #4B0082;}\
      28%  {background: #0000FF; border: #0000FF;}\
      42%  {background: #00FF00; border: #00FF00;}\
      56%  {background: #FFFF00; border: #FFFF00;}\
	  70%  {background: #FF7F00; border: #FF7F00;}\
	  84%  {background: #FF0000; border: #FF0000;}\
	  100% {background: #9400D3; border: #9400D3;}\
}\
\
@-webkit-keyframes colorchangeBorderOnly {\
      0%   {border: #9400D3;}\
      14%  {border: #4B0082;}\
      28%  {border: #0000FF;}\
      42%  {border: #00FF00;}\
      56%  {border: #FFFF00;}\
	  70%  {border: #FF7F00;}\
	  84%  {border: #FF0000;}\
	  100% {border: #9400D3;}\
}";
document.body.appendChild(styleElement);

/* Adds the animations. */
bar.style.webkitAnimation = "colorchange 7s infinite";

/* Last function is void to prevent the function to print a returning value. */
void(searchbox.style.webkitAnimation = "colorchangeBorderOnly 7s infinite");