# SegaSaturnFilmMuxer

This tool will take the Audio and Video from 2 different source FILM files and remux them to create a new FILM file of the combined streams. This is useful if you need to modify only one aspect of an FMV in a Saturn game such as the following situations:

1) You have a Japanese FMV that you want to replace the Audio with a different langauges dub while preserving the quality of the original Japanese Video encode.
2) You have a newly encoded FMV but you want to preserve the quality or compression scheme of the original FMV from the game.

The tool is able to support both uncompressed PCM audio as well as ADX compressed audio.

# Requirements for use

Currently the tool does have the following requirements for use:

1) Both files video streams must have the exact same amount of frames and match in resolution and frame rate.
2) Both files audio streams must have the same length and specifications (8-bit/16-bit, Mono/Stereo, Sample Rate, etc.). Audio Compression however does not matter.
3) Being written in Java, a Java Runtime Environment must be installed. It should work with Java 8 or higher.

The one obvious caveat here is that you need to be able to encode your videos in the Saturn format to start. For ADX audio the key caveat is you need a source file that already has ADX audio. This can work if you want to preserve an original FMV's ADX audio, but unfortunately there isn't a way to add new ADX audio yet. I'd like to add this in the future, but I just haven't had the time to look into how this kind of audio was originally broken up into it's STAB chunks.

# How to use

1) Encode your new video as you normally would following the Sega Saturn Cinepak Encoding process making sure to adhear to the requirements listed above.
    * If you want to preserve the original video but replace the audio, encode a copy of the source video with your new audio swapped in.
    * If you want to preserve the original audio, encode your modified video with uncompressed PCM audio that matches the specifications of the original source video.
2) Run the tool and select the source FILM file for your audio, the source FILM file for your video, and the output directory.
3) Click the "Mux Audio and Video" button.

Your new file will be in the specified output directory with the naming convention "NEW_<Name_of_Source_Audio_File>".

# Why use this instead of FFMPEG?

While FFMPEG does technically support Sega FILM files, it does not generate the STAB chunk correctly. While some games may be lenient and still play these files, thay may present issues (Video glitches, Cracks and Pops in audio, other errors, etc.). Other games may instead just flat out refuse to play these files or crash completely. A good example of this is Sakura Wars 2. When doing research on various different ADX Cinepak files, this was a game I found that flat out would not play any file I threw at it that used ADX audio that FFMPEG created.

This tool will instead generate a correct file with a compliant STAB chunk that any game should be able to play. To show that it works, here is a test I did taking the Disc 2 intro for Sakura Wars 2, and adding subtitles that I did for the first game and patching it into Disc 1:

https://www.youtube.com/watch?v=hf_0NowZuV8
