# SegaSaturnFilmMuxer


This tool will take the Audio and Video from 2 different sources and remux them to create a new FILM file of the combined streams. This is useful if you need to modify only one aspect of an FMV in a Saturn game such as the following situations:

1. You have a Japanese FMV that you want to replace the Audio with a different langauges dub while preserving the quality of the original Japanese Video encode.

2. You have a newly encoded FMV but you want to preserve the quality or compression scheme of the original FMV from the game.
   
The tool is able to support both uncompressed PCM audio as well as ADX compressed audio.


# Requirements for use


Currently the tool does have the following requirements for use:
1. If using 2 videos as sources, both files video streams must have the exact same amount of frames and match in resolution and frame rate.

2. If swapping audio for translation/modification purposes or if using uncompressed audio, both files audio streams should have the same specifications (8-bit/16-bit, Mono/Stereo, Sample Rate, length, etc.). Audio Compression however does not matter.
  
3. Being written in Java, a Java Runtime Environment must be installed. It should work with Java 8 or higher.


NOTES FOR ADX AUDIO:

Muxing existing ADX audio from a source FILM file with a new video stream from another FILM file is supported and requires he user follow the requirements stated above.

If you are swapping in new ADX audio to mux with an existing Video Stream, you only need to provide the ADX file for the Source Audio file. The one caveat is that the new ADX file must be the exact same specifications (Sample Rate, Stereo/Mono, etc.) as the original you are replacing. It should also be the exact same size in bytes. This is to keep the file as close to the original specifications the game is expecting.

If you are muxing ADX into a file that does not already use ADX audio, the specifications of the ADX audio file will be used. Keep in mind that this may push the video file beyond it's bitrate limit. This feature is also highly experimental as there is no real way to test these files yet beyond injecting them into games that already use ADX Cinepak.

NOTES for PCM AUDIO:

Uncompressed PCM Audio is supported in the following file formats:

.PCM File - 8-bit and 16-bit Audio are supported. These files are assumed to be raw headerless files. The default setting assumes they are in Little Endian format. If stereo it assumes they channels are in the standard interleaved format. 

 - If your file is already in Big Endian format you need to check the Big Endian box. 
 - If your PCM file is one that was extracted from an existing FILM file and is in the raw Saturn Format, then check the Saturn Format box.

.WAV File - Only 16-bit is supported. This is becasue Saturn FILM files use 8-bit SIGNED PCM. The WAV format assumes 8bit is unsigned, so Signed 8-bit is an invalid format. 

NOTE: 
 - When using WAV or ADX audio, the SaturnFormat and BigEndian checkboxes are ignored as these aren't releavnt to these file formats.

# How to use

1. If you need to encode a new video, encode it as you normally would following the Sega Saturn Cinepak Encoding process making sure to adhear to the requirements listed above.

    * If you want to preserve the original video but replace the audio and dont want to use a WAV or PCM file, encode a copy of the source video with your new audio swapped in.
    * If you want to preserve the original audio, encode your modified video with uncompressed PCM audio that matches the specifications of the original source video.

2. Run the tool and select the source FILM, PCM, WAV or ADX file for your audio, the source FILM file for your video, and the output directory.

4. If using PCM check any special options check boxes if required. (Big Endian, Saturn format, Etc.)

3. Click the "Mux Audio and Video" button.

Your new file will be in the specified output directory with the naming convention "NEW_<Name_of_Source_Audio_File>".

# Audio Extraction

Audio Extraction is a new feature added. Both PCM and ADX audio can be extracted from a source FILM file and can be found under the extract tab.

* If your source uses ADX, the file will be extracted as a .ADX file.
*  If it uses 8-bit Uncompressed PCM, it will be extracted as a headerless .PCM file ready to be imported into Audacity as RAW Audio.
*  If it's 16-bit PCM, you can either extract it as a Headerless .PCM file ready to import into Audacity as RAW Audio, or you can export it as a WAV file by checking the WAV Output box.

# Why use this instead of FFMPEG?

While FFMPEG does technically support Sega FILM files, it does not generate the STAB chunk correctly. While some games may be lenient and still play these files, thay may present issues (Video glitches, Cracks and Pops in audio, other errors, etc.). Other games may instead just flat out refuse to play these files or crash completely. A good example of this is Sakura Wars 2. When doing research on various different ADX Cinepak files, this was a game I found that flat out would not play any file I threw at it that used ADX audio that FFMPEG created.

This tool will instead generate a correct file with a compliant STAB chunk that any game should be able to play. To show that it works, here is a test I did taking the Disc 2 intro for Sakura Wars 2, and adding subtitles that I did for the first game and patching it into Disc 1:

https://www.youtube.com/watch?v=hf_0NowZuV8

