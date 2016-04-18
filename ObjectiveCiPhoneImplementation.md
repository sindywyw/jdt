Details of the Objective C implementation of the JDT for the iPhone device<br>
<h1>Introduction</h1>

The jdt application was re-implemented in the Objective C language as an iPhone application.<br>
Most of the original jdt's application features were introduced to this version.<br>

<h1>Details</h1>

<b>The major challenges for this task are:</b><br>
<ul><li>The constraints of a mobile device (memory, user interface, hd...)<br>
</li><li>The variation between Java and Objective C<br></li></ul>

<h2>Content</h2>
The following features of the original application were introduced to the iPhone version:<br>
<ul><li>Loading and saving tsin/smf files.<br>
</li><li>Input methods - Point, 100 Random points<br>
</li><li>View options - Lines, Triangles, Topo, Find, Info<br></li></ul>

<h2>Implementation</h2>
The is implemented as a tab bar application which has 3 tabs:<br>
<h3>Resources</h3>
A tab in which the user can store or load <i>tsin</i> and <i>smf</i> files.<br>
<b>See screenshot:</b><br>
<a href='http://dl.dropbox.com/u/2835100/OCDT/Screenshots/tab_resources.PNG'>http://dl.dropbox.com/u/2835100/OCDT/Screenshots/tab_resources.PNG</a>
<br>
<br>
<h3>Settings</h3>
A tab which enables the user to choose between the Input methods and the View options + to clear the screen.<br>
<b>See screenshot:</b><br>
<a href='http://dl.dropbox.com/u/2835100/OCDT/Screenshots/tab_settings.PNG'>http://dl.dropbox.com/u/2835100/OCDT/Screenshots/tab_settings.PNG</a>
<br>
<br>
<h3>Display</h3>
Display the current triangulation according to the settings.<br>
The display tab contains a user interface called <i>UIScrollView</i> which is a container for the triangulation graphics.<br>
The <i>UIScrollView</i> enables scrolling + zooming in and out so that an image that is larger than the actual screen can be seen easily.<br>
See screenshots:<br>
<br>
<b>Manual point insertions:</b><br>
<a href='http://dl.dropbox.com/u/2835100/OCDT/Screenshots/input_point.PNG'>http://dl.dropbox.com/u/2835100/OCDT/Screenshots/input_point.PNG</a>
<br><br>
<b>Map topography view:</b><br>
<a href='http://dl.dropbox.com/u/2835100/OCDT/Screenshots/view_topography.PNG'>http://dl.dropbox.com/u/2835100/OCDT/Screenshots/view_topography.PNG</a>
<br><br>
<h1>Performance</h1>
See <a href='ObjectiveCiPhonePerformance.md'>Objective C iPhone Performance page</a>