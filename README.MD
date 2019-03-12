# PlexHoles

[![Build Status](https://travis-ci.org/lemker/PlexHoles.svg?branch=master)](https://travis-ci.org/lemker/PlexHoles)

## About
A quick way to find missing content in your existing Plex Media Server library - 
PlexHoles will iteratively work through your library to find missing TV episodes using the TVDB API as well as your Plex server metadata to match unique series data.
It will do its best to match series/episode data, however if you find any error please submit an issue above.

PlexHoles supports SSL/HTTPS connections to Plex and TVDB authentication servers. It can also safely connect using self-signed certificates, used by your self-hosted Plex Media Server.

## Getting Started
To use this application you must have the following prerequisites:
1) A Plex account. You can get one at https://www.plex.tv/
2) Downloaded and installed the Plex Media Server application onto your local or remote machine. Download at https://www.plex.tv/media-server-downloads/#plex-media-server 
3) Linked your Plex Server to your Plex account.
4) Added some media to the server
5) TVDB API key - https://api.thetvdb.com/swagger

## How It Works
#### Step 1 - Plex Token
The program will attempt a POST request to get a Plex authentication token from https://plex.tv/. If successful, a header object is created with program information as well as the authentication token from the POST request.

#### Step 2 - TVDB Token
The program will attempt a POST request to get a TVDB token from https://api.thetvdb.com/. If successful, a header object is created using the authentication token information received from the TVDB API. 

#### Step 3 - Plex Media Container Scrape
The program will look for available media container keys. Each media container that matches either type "show" or type "movie" will be individually processed.

#### Step 4 - Plex Media Scrape
The program will create "series" objects that hold important information about each series in your Plex Media Server library. Each object holds episode data, as well as a GUID used for TVDB API matching.

#### Step 5 - TVDB API
The program will use the gathered GUID information from your library to download and parse the correct series information from the TVDB API. Episodes with certain characteristics, such as "Season 0" or the aired date after the current date are skipped.

#### Step 6 - Find Missing Content
Lastly, the program will attempt to match Plex media data to TVDB media data. Content that cannot be matched is marked as "missing" from your library.

## TODO
- Sometimes series data is duplicated while parsing Plex library
- Sometimes series data is not found on TVDB
- Add functionality to match top 250/500/1000 movies from IMDB