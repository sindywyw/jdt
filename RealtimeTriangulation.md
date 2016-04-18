# Introduction #

This page explains the usage of the **realtime** package of the jdt project.
This package, together with the real-time classes added at the gui package, allow
to use the jdt library as a **Realtime triangulation and simplification of terrains** library.


# Details #

Until now, the jdt library only allowed triangulation of a list of points that was received as input from a .tsin or .smf file.
Alas, the triangulation process takes quite a bit of time. In order to lower the number of points that the triangulation is computed on, a program called **Terra** is used.

**Terra** was developed by **Michael Garland** along with other programs, such as **QSlim** and **Scape**. Terra receives a height-map as input (.pgm file) and outpus a simplified one.
Terra can also output the triangulation of the height-map.

Some advantages of Terra are that it was written entirely in C++ as a command-line application, which makes it extra fast (as opposed to code written in Java) and very easy to use.

**More can be read about Terra, how to get it and the algorithm that is used to simplify the Terrain here**: http://mgarland.org/software/terra.html.


# Realtime Simulation #

In order to test the Realtime triangulation and simplification, frames taken from a **Time of Flight** camera were used.

These frames are the output of **Mesa-Imaging's SR4000 camera**. These frames are .dat format files and thus must be converted to .pgm format, something that can be seen in the **realtime** package - the DatToPgmTranslator class. This class converts the distance (Z coordinates) values from the .dat files and creates a .pgm file on disk.

More can be read about the SR4000 camera here: http://www.mesa-imaging.ch/.

To simulate the stream of data from a Time of Flight camera, these frames were saved on the HD and were then parsed, one by one, by an instance of the DatStreamer class. This class block until the next frame exists. The DatStreamer class extends the TerrainStramer abstract class which represents the functionality of each Streamer class.

It is very easy to extend this functionality and create some other Streamer class which will stream data from some other source.


# Realtime Data Processing #

The process of the Realtime triangulation and simplification can be seen here:

![http://img37.imageshack.us/img37/6431/93424716.png](http://img37.imageshack.us/img37/6431/93424716.png)


# Usage #

First, you should have some .dat frames on the HD.

NOTE: the frames must be named "0.dat", "1.dat", "2.dat" and so on.
You can change the filenames manually or write an automatic script.

Open the eclipse project and run the **RealtimeFrame**.
Press the **"Start"** button and choose the executable file of Terra.
Now, you will must enter the path to the directory of the .dat files. This folder will also be used to save .pgm and .smf files while processing data in real-time.

Now, the original, simplified and triangulated frame will be presented.

For your convenience, you can pause the processing using the **"Pause"** button, and even view an old frame using the **"Go to Frame**" button.

You can also choose to show only the original frame and each one of the simplified/triangulated frame using the **"Simplify"** checkbox and the **"Triangulate"** checkbox
and the bottom of the frame.

**Enjoy!**


