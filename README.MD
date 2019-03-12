# PlexHoles

[![Build Status](https://travis-ci.org/lemker/PlexHoles.svg?branch=master)](https://travis-ci.org/lemker/PlexHoles)

## About
A quick way to find missing content in your existing Plex Media Server library - 
PlexHoles will iteratively work through your library to find missing TV episodes using the TVDB API as well as your Plex server metadata to match unique series data.
It will do its best to match series/episode data, however if you find any error please submit an issue above.

### Getting Started
To use this application you must have completed the following prerequisites:
1) A Plex account. You can get one at https://www.plex.tv/
2) Downloaded and installed the Plex Media Server application onto your local or remote machine. Download at https://www.plex.tv/media-server-downloads/#plex-media-server 
3) Linked your Plex Server to your Plex account.
4) Added some media to the server
5) TVDB API key - https://api.thetvdb.com/swagger

### TODO
- Sometimes series data is duplicated while parsing Plex library
- Sometimes series data is not found on TVDB
- Add functionality to match top 250/500/1000 movies from IMDB