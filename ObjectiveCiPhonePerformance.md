Performance and measurements of the iPhone objective C implementation<br>

<h1>Introduction</h1>

One of the tasks of this exercise is to compare the implementation's performance to the original implementation.<br>
For this we will perform run time measurements of the application features.<br>

<h1>Details</h1>
The measurements were done on the iPhone device itself and on an iPhone simulator on a Mac Book Air device.<br>
All measurements were done on the 1000 points files since the iPhone 2G could not handle bigger files.<br>
<h2>Devices Information</h2>
The iPhone device:<br>
<ul><li>Hardware: iPhone 2G see <a href='http://www.gsmarena.com/apple_iphone-1827.php'>Hardware Specification</a><br>
</li><li>OS: 3.0<br>
</li><li>Firmware: 04.05.04_G<br>
The Simulator on Mac Book Air device:<br>
</li><li>Hardware: Mac Book Air 1.86 Ghz, see <a href='http://www.apple.com/macbookair/specs.html'>Hardware Specification</a><br>
</li><li>OS: OS X 10.6.1<br>
<h2>Tested Features</h2>
<h3>File triangulation</h3>
iPhone Simulator: <br>
</li><li>il_1000.smf - 0.179 sec<br>
</li><li>t1-1000.tsin - 0.408 sec<br>
iPhone Device:<br>
</li><li>il_1000.smf - 1.986 sec<br>
</li><li>t1-1000.tsin - 5.351 sec<br>
<h3>Topography</h3>
iPhone Simulator: <br>
</li><li>il_1000.smf - 0.07 sec<br>
</li><li>t1-1000.tsin - 0.128 sec<br>
iPhone Device:<br>
</li><li>il_1000.smf - 1.598 sec<br>
</li><li>t1-1000.tsin - 3.204 sec<br>
<h3>Find</h3>
Find a triangle that is in the middle of the view<br>
iPhone Simulator: <br>
</li><li>il_1000.smf - 0.001 sec<br>
</li><li>t1-1000.tsin - 0.001 sec<br>
iPhone Device:<br>
</li><li>il_1000.smf - 0.006 sec<br>
</li><li>t1-1000.tsin - 0.007 sec<br>