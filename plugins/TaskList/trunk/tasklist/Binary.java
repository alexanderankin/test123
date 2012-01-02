package tasklist;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.MiscUtilities;

/**
 * This class has a large list of file types by file name extension. This list
 * is not all inclusive, but covers many of the more widely used file formats.
 * This class is to help identify binary files prior to loading them in a buffer,
 * which helps overall performance and helps reduce error messages.
 */
public class Binary {
    // This list is from Wikipedia. There may be duplicates.
    static String[] binary = {
        //Main article: List of archive formats
        ".7z", // 7-Zip compressed file
        ".aac", // Advanced Audio Coding
        ".ace", // ACE compressed file
        ".alz", // ALZip compressed file
        ".apk", // Applications installable on Android
        ".at3", // Sony's UMD Data compression
        ".bke", // BackupEarth.com Data compression
        ".arc",
        ".arj", // ARJ compressed file
        ".ba", // Scifer Archive (.ba), Scifer External Archive Type
        ".big", // Special file compression format used by Electronic Arts for compressing the data for many of EA's games
        ".bik", // Bink Video file. A video compression system developed by RAD Game Tools
        ".bkf", // Microsoft backup created by NTBACKUP.EXE
        ".bzip2", // (.bz2)
        ".bmp", // (paint)
        ".c4", // JEDMICS image files, a DOD system
        ".cab", // Microsoft Cabinet
        ".cals", // JEDMICS image files, a DOD system
        ".sea", // Compact Pro (Macintosh)
        ".sea", // Compact Pro (Macintosh)
        ".daa", // Closed-format, Windows-only compressed disk image
        ".deb", // Debian Linux install package
        ".dmg", // an Apple compressed/encrypted format
        ".ddz", // a file which can only be used by the "daydreamer engine" created by "fever-dreamer", a program similar to RAGS, it's mainly used to make somewhat short games.
        ".eea", // An encrypted CAB, ostensibly for protecting email attachments
        ".egg", // Alzip Egg Edition compressed file
        ".egt", // EGT Universal Document also used to create compressed cabinet files replaces .ecab
        ".ecab", // EGT Compressed Folder used in advanced systems to compress entire system folders, replaced by EGT Universal Document
        ".ezip", // EGT Compressed Folder used in advanced systems to compress entire system folders, replaced by EGT Universal Document
        ".ess", // EGT SmartSense File, detects files compressed using the EGT compression system.
        ".gho", // Norton Ghost
        ".ghs", // Norton Ghost
        ".gz", // Compressed file
        ".ipg", // Format in which Apple Inc. packages their iPod games. can be extracted through Winrar
        ".jar", // ZIP file with manifest for use with Java applications.
        ".lbr", // Library file
        ".lqr", // LBR Library file compressed by the SQ program.
        ".lzh", // Lempel, Ziv, Huffman
        ".lzo",
        ".lzma",
        ".lzx",
        ".mbw",
        ".mpq", // Used by Blizzard games
        ".bin",
        ".nth", // Nokia Theme Used by Nokia Series 40 Cellphones
        ".pak", // Enhanced type of .ARC archive
        ".par",
        ".par2",
        ".pk3", // (See note on Doom³)
        ".pk4", // (Opens similarly to a zip archive.)
        ".rar", // for multiple file archive (rar to .r01-.r99 to s01 and so on)
        ".rag", // RAGS game file, a game playable in the RAGS game-engine, a free program which both allows people to create games, and play games, games created have the file format "rag game file",
        ".sen", // Scifer Internal Archive Type
        ".sit", // StuffIt (Macintosh)
        ".sitx", // StuffIt (Macintosh)
        ".tar",
        ".tar.gz", // gzipped tar file
        ".tgz", // gzipped tar file
        ".tb", // Tabbery Virtual Desktop Tab file
        ".tib", // Acronis True Image backup
        ".uha", // Ultra High Archive Compression
        ".viv", // Archive format used to compress data for several video games, including Need For Speed: High Stakes.
        ".vol", // unknown archive
        ".vsa", // Altiris Virtual Software Archive
        ".wax", // Wavexpress", // A ZIP alternative optimized for packages containing video, allowing multiple packaged files to be all-or-none delivered with near-instantaneous unpacking via NTFS file system manipulation.
        ".z", // Unix compress file
        ".zoo",
        ".zip",
        //  Physical recordable media archiving
        ".iso", // The generic file format for most optical media, including CD-ROM, DVD-ROM, Blu-ray Disc, HD DVD and UMD. ISO images indicate write directives, while .bin files that usually accompany such files contain the actual data.
        ".nrg", // The proprietary optical media archive format used by Nero applications.
        ".img", // For archiving MS-DOS formatted floppy disks.
        ".adf", // Amiga Disk Format, for archiving Amiga floppy disks
        ".adz", // The GZip-compressed version of ADF.
        ".dms", // Disk Masher System, a disk-archiving system native to the Amiga.
        ".dsk", // For archiving floppy disks from a number of other platforms, including the ZX Spectrum and Amstrad CPC.
        ".d64", // An archive of a Commodore 64 floppy disk.
        ".sdi", // System Deployment Image, used for archiving and providing "virtual disk" functionality.
        ".mds", // DAEMON tools native disc image file format used for making images from optical CD-ROM, DVD-ROM, HD DVD or Blu-ray Disc. It comes together with MDF file and can be mounted with DAEMON Tools or Alcohol 120% software.
        ".mdx", // New DAEMON Tools file format that allows to get one MDX disc image file instead of two (MDF and MDS).
        ".dmg", // Macintosh disk image files
        ".cdi", // DiscJuggler image file
        ".cue", // CDRWrite CUE image file
        ".cif", // Easy CD Creator .cif format
        ".c2d", // Roxio / WinOnCD .c2d format
        ".daa", // PowerISO .daa format
        ".ccd,sub,img", // CloneCD image file
        ".b6t", // BlindWrite 5/6 image file
        
        //  Computer-aided
        ".3dmlw", // (3D Markup Language for Web) files
        ".3dxml", // Dassault Systemes graphic representation
        ".acp", // VA Software VA", // Virtual Architecture CAD file
        ".amf", // Additive Manufacturing File Format
        ".ar", // Ashlar-Vellum Argon", // 3D Modeling
        ".art", // ArtCAM model
        ".asc", // BRL-CAD Geometry File (old ASCII format)
        ".asm", // Solidedge Assembly, Pro/ENGINEER Assembly
        ".bim", // Data Design System DDS-CAD
        ".ccc", // CopyCAD Curves
        ".ccm", // CopyCAD Model
        ".ccs", // CopyCAD Session
        ".cad", // CadStd
        ".catdrawing", // CATIA V5 Drawing document
        ".catpart", // CATIA V5 Part document
        ".catproduct", // CATIA V5 Assembly document
        ".catprocess", // CATIA V5 Manufacturing document
        ".cgr", // CATIA V5 graphic representation file
        ".co", // Ashlar-Vellum Cobalt", // parametric drafting and 3D modeling
        ".drw", // Caddie Early version of Caddie drawing", // Prior to Caddie changing to DWG
        ".dwg", // AutoCAD and Open Design Alliance applications
        ".dft", // Solidedge Draft
        ".dgn", // MicroStation design file
        ".dgk", // Delcam Geometry
        ".dmt", // Delcam Machining Triangles
        ".dxf", // ASCII Drawing Interchange file format", // AutoCAD
        ".dwb", // VariCAD drawing file
        ".dwf", // AutoDesk's Web Design Format; AutoCAD & Revit can publish to this format; similar in concept to PDF files; AutoDesk Design Review is the reader
        ".emb", // Wilcom ES Designer Embroidery CAD file
        ".esw", // Agtek format
        ".excellon", // Excellon file
        ".exp", // Drawing Express file format
        ".fm", // FeatureCAM Part File
        ".fmz", // FormZ Project file
        ".g", // BRL-CAD Geometry File
        ".gerber", // Gerber file
        ".grb", // T-FLEX CAD File
        ".gtc", // GRAITEC Advance file format
        ".iam", // Autodesk Inventor Assembly file
        ".icd", // IronCAD 2D CAD file
        ".idw", // Autodesk Inventor Drawing file
        ".ifc", // buildingSMART for sharing AEC and FM data
        ".iges", // Initial Graphics Exchange Specification
        ".ipn", // Autodesk Inventor Presentation file
        ".ipt", // Autodesk Inventor Part file
        ".mcd", // Monu-CAD (Monument/Headstone Drawing file)
        ".model", // CATIA V4 part document
        ".ocd", //Orienteering Computer Aided Design (OCAD) file
        ".par", // Solidedge Part
        ".prt", // NX (recently known as Unigraphics), Pro/ENGINEER Part, CADKEY Part
        ".pln", // ArchiCad project
        ".psm", // Solidedge Sheet
        ".psmodel", // PowerSHAPE Model
        ".pwi", // PowerINSPECT File
        ".pyt", // Pythagoras File
        ".skp", // SketchUp Model
        ".rlf", // ArtCAM Relief
        ".rvt", // AutoDesk Revit project files
        ".rfa", // AutoDesk Revit family files
        ".scdoc", // SpaceClaim 3D Part/Assembly
        ".sldasm", // SolidWorks Assembly drawing
        ".slddrw", // SolidWorks 2D drawing
        ".sldprt", // SolidWorks 3D part model
        ".step", // Standard for the Exchange of Product model data
        ".stl", // Stereo Lithographic data format (see STL (file format)) used by various CAD systems and stereo lithographic printing machines.
        ".tct", // TurboCAD drawing template
        ".tcw", // TurboCAD for Windows 2D and 3D drawing
        ".unv", // I-DEAS I-DEAS (Integrated Design and Engineering Analysis Software)
        ".vc6", // Ashlar-Vellum Graphite", // 2D and 3D drafting
        ".vlm", // Ashlar-Vellum Vellum, Vellum 2D, Vellum Draft, Vellum 3D, DrawingBoard
        ".vs", // Ashlar-Vellum Vellum Solids
        ".wrl", // Similar to STL, but includes color. Used by various CAD systems and 3D printing rapid prototyping machines. Also used for VRML models on the web.
        ".xe", // Ashlar-Vellum Xenon", // for Associative 3D Modeling
        
        //  Electronic design automation (EDA)
        ".brd", // Board file for EAGLE Layout Editor, a commercial PCB design tool
        ".bsdl", // Description language for testing through JTAG
        ".cdl", // Transistor-level netlist format for IC design
        ".cpf", // Power-domain specification in SoC implementation (see also UPF)
        ".def", // Gate-level layout
        ".dspf", // Detailed Standard Parasitic Format, Analog-level parasitics of interconnections in IC design
        ".edif", // Vendor neutral gate-level netlist format
        ".fsdb", // Analog waveform format (see also Waveform viewer)
        ".gdsii", // Format for PCB and layout of integrated circuits
        ".hex", // ASCII-coded binary format for memory dumps
        ".lef", // Library Exchange Format, physical abstract of cells for IC design
        ".lib", // Library modeling (function, timing) format
        ".ms10", // NI Multisim file
        ".oasis", // Open Artwork System Interchange Standard
        ".openaccess", // Design database format with APIs
        ".sdc", // Synopsys Design Constraints, format for synthesis constraints
        ".sdf", // Standard for gate-level timings
        ".spef", // Standard format for parasitics of interconnections in IC design
        ".spi, cir", // SPICE Netlist, device-level netlist and commands for simulation
        ".srec, s19", // S-record, ASCII-coded format for memory dumps
        ".sv", // SystemVerilog source file
        ".upf", // Standard for Power-domain specification in SoC implementation
        ".v", // Verilog source file
        ".vcd", // Standard format for digital simulation waveform
        ".vhd, vhdl", // VHDL source file
        
        //  Database
        ".accdb", // Microsoft Database (Microsoft Office Access 2007)
        ".adt", // Sybase Advantage Database Server (ADS)
        ".apr", // Lotus Approach data entry & reports
        ".box", // Lotus Notes Post Office mail routing database
        ".chml", // Krasbit Technologies Encrypted database file for 1 click integration between contact management software and the chameleon(tm) line of imaging workflow solutions
        ".daf", // Digital Anchor data file
        ".dat", // DOS Basic
        ".db", // Paradox, SQLLite, others
        ".dbf", // db/dbase II,III,IV and V, Clipper, Harbour/xHarbour, Fox/FoxPro, Oracle
        ".egt", // EGT Universal Document, used to compress sql databases to smaller files, may contain original EGT database style.
        ".ess", // EGT SmartSense is a database of files and its compression style. Specific to EGT SmartSense
        ".eap", // Enterprise Architect Project
        ".fdb", // Firebird Databases, Navision database file
        ".fp", // FileMaker Pro
        ".fp3",
        ".fp5",
        ".fp7",
        ".frm", // MySQL table definition
        ".gdb", // Borland InterBase Databases
        ".kexi", // Kexi database file (SQLite-based)
        ".kexic", // shortcut to a database connection for a Kexi databases on a server
        ".ldb", // Temporary database file, only existing when database is open
        ".mdb", // Microsoft Database (Access)
        ".ldb",
        ".adp", // Microsoft Access project (used for accessing databases on a server)
        ".mde", // Compiled Microsoft Database (Access)
        ".mdf", // Microsoft SQL Server Database
        ".myd", // MySQL MyISAM table data
        ".myi", // MySQL MyISAM table index
        ".ncf", // Lotus Notes configuration file
        ".nsf", // Lotus Notes database
        ".ntf", // Lotus Notes database design template
        ".nv2", // QW Page NewViews object oriented accounting database
        ".odb", // OpenDocument database
        //".ora", // Oracle tablespace files sometimes get this extension (also used for configuration files)
        ".pdb", // Palm OS Database
        ".pdi", // Portable Database Image
        ".pdx", // Corel Paradox database management
        ".prc", // Palm OS resource database
        //".sql", // bundled SQL queries
        ".rel", // Sage Retrieve 4GL data file
        ".rin", // Sage Retrieve 4GL index file
        ".sdb", // StarOffice's StarBase
        ".udl", // Universal Data Link
        ".wdb", // Microsoft Works Database
        ".wmdb", // Windows Media Database file
        
        //  Desktop publishing
        ".dtp", // greenstreet Publisher, GST PressWorks
        ".indd", // Adobe InDesign
        ".mcf", // FotoInsight Designer
        ".pmd", // Adobe PageMaker
        ".ppp", // Serif PagePlus
        ".pub", // Microsoft Publisher
        ".fm", // Adobe FrameMaker
        
        //  Document
        //These files store formatted text and plain text. 
        // I assume most of these are binary.
        ".602", // Text602 document
        ".abw", // AbiWord document
        ".acl", // MS Word AutoCorrect List
        ".afp", // Advanced Function Presentation", // IBc
        //".ans", // ANSI text with Layout
        //".asc", // ASCII text with Layout
        ".aww", // Ability Write
        ".ccf", // Color Chat 1.0
        //".csv", // ASCII text encoded as Comma Separated Values, used in most spreadsheets such as Microsoft Excel or by most database management systems
        ".cwk", // ClarisWorks / AppleWorks document
        ".doc", // Microsoft Word document
        ".docx", // Office Open XML Text document or Microsoft Office Word 2007 for Windows/2008 for Mac
        ".dot", // Microsoft Word document template
        ".dotx", // Office Open XML Text document template
        ".egt", // EGT Universal Document
        ".fdx", // Final Draft
        ".ftm", // Fielded Text Meta
        ".ftx", // Fielded Text (Declared)
        //".html", // HyperText Markup Language (.html, .htm)
        ".hwp", // Haansoft (Hancom) Hangul Word Processor document
        ".hwpml", // Haansoft (Hancom) Hangul Word Processor Markup Language document
        ".lwp", // Lotus Word Pro
        ".mcw", // Microsoft Word for Macintosh (versions 4.0–5.1)
        ".nb", // Mathematica Notebook
        ".nbp", // Mathematica Player Notebook
        ".odm", // OpenDocument Master document
        ".odt", // OpenDocument Text document
        ".ott", // OpenDocument Text document template
        ".omm", // OmmWriter Text document
        ".pages", // Apple Pages document
        ".pap", // Papyrus word processor document
        ".pdax", // Portable Document Archive (PDA) document index file
        ".pdf", // Portable Document Format
        ".rtf", // Rich Text document
        ".quox", // Question Object File Format Question Object document for Quobject Designer or Quobject Explorer
        ".rpt", // Crystal Reports
        ".sdw", // StarWriter text document, used in earlier versions of StarOffice
        ".stw", // OpenOffice.org XML (obsolete) text document template
        ".sxw", // OpenOffice.org XML (obsolete) text document
        ".tex",
        ".info",
        //".txt", // ASCII or Unicode plaintext
        ".uof", // Uniform Office Format
        ".uoml", // UniqueObject Markup Language (UOML) is a XML-based markup language; uniqueobject
        ".wpd", // WordPerfect document
        ".wps", // Microsoft Works document
        ".wpt", // Microsoft Works document template
        ".wrd", // WordIt! Document
        ".wrf", // ThinkFree Write
        ".wri", // Microsoft Write document
        //".xhtml, .xht", // eXtensible Hyper-Text Markup Language
        //".xml", // eXtensible Markup Language
        ".xps", // Open XML Paper Specification
        
        //  Font file
        ".abf", // Adobe Binary Screen Font
        ".afm", // Adobe Font Metrics
        ".bdf", // Bitmap Distribution Format
        ".bmf", // ByteMap Font Format
        ".fnt", // Bitmapped Font", // Graphical Environment Manager
        ".fon", // Bitmapped Font", // Microsoft Windows
        ".mgf", // MicroGrafx Font
        ".otf", // OpenType Font
        ".pcf", // Portable Compiled Font
        ".postscript", // Type 1, Type 2
        ".pfa", // Printer Font ASCII
        ".pfb", // Printer Font Binary", // Adobe
        ".pfm", // Printer Font Metrics", // Adobe
        ".fond", // Font Description resource", // Mac OS
        ".sfd", // FontForge spline font database Font
        ".snf", // Server Normal Format
        ".tdf", // TheDraw Font
        ".tfm", // TeX font metric
        ".ttf", // TrueType Font
        ".ttc", // TrueType Font
        
        //  Geographic information system
        ".apr", // ESRI ArcView 3.3 and earlier project file
        ".dem", // USGS DEM file format
        ".e00", // ARC/INFO interchange file format
        ".geotiff", // Geographically located raster data
        //".gpx", // XML-based interchange format
        ".mxd", // ESRI ArcGIS project file, 8.0 and higher
        ".shp", // ESRI shapefile
        //".tab", // MapInfo Table file format
        ".dted", // Digital Terrain Elevation Data
        //".kml", // Keyhole Markup Language, XML-based
        
        //  Graphical information organizers
        ".3dt", // 3D Topicscape The database in which the meta-data of a 3D Topicscape is held. A 3D Topicscape is a form of 3D concept map (like a 3D mind-map) used to organize ideas, information and computer files.
        ".aty", // 3D Topicscape file, produced when an association type is exported by 3D Topicscape. Used to permit round-trip (export Topicscape, change files and folders as desired, re-import them to 3D Topicscape).
        ".cag", // Linear Reference System.
        ".fes", // 3D Topicscape file, produced when a fileless occurrence in 3D Topicscape is exported to Windows. Used to permit round-trip (export Topicscape, change files and folders as desired, re-import them to 3D Topicscape).
        ".mgmf", // MindGenius Mind Mapping Software file format.
        //".mm", // FreeMind mind map file (XML).
        ".mmp", // Mind Manager mind map file.
        ".tpc", // 3D Topicscape file, produced when an inter-Topicscape topic link file is exported to Windows. Used to permit round-trip (export Topicscape, change files and folders as desired, re-import them to 3D Topicscape).
        
        //  Graphics
        //  Color palettes
        ".act", // Adobe Color Table. Contains a raw color palette and consists of 256 24-bit RGB colour values.
        ".pal", // Microsoft palette file
        
        //  Raster graphics
        ".ase", // Adobe Swatch
        ".art", // America Online proprietary format
        ".bmp", // Microsoft Windows Bitmap formatted image
        ".blp", // Blizzard Entertainment proprietary texture format
        ".cit", // Intergraph is a monochrome bitmap format
        ".cpt", // Corel PHOTO-PAINT image
        ".cut", // Dr. Halo image file
        ".dds", // DirectX texture file
        ".dib", // Device-Independent Bitmap graphic
        ".djvu", // DjVu for scanned documents
        ".egt", // EGT Universal Document, used in EGT SmartSense to compress PNG files to yet a smaller file
        ".exif", // Exchangeable image file format (Exif) is a specification for the image file format used by digital cameras
        ".gif", // CompuServe's Graphics Interchange Format
        ".gpl", // GIMP Palette, using a textual representation of color names and RGB values
        ".icns", // file format use for icons in Mac OS X. Contains bitmap images at multiple resolutions and bitdepths with alpha channel.
        ".ico", // a file format used for icons in Microsoft Windows. Contains small bitmap images at multiple resolutions and sizes.
        ".iff", // ILBM
        ".ilbm", // ILBM
        ".lbm", // ILBM
        ".jng", // a single-frame MNG using JPEG compression and possibly an alpha channel.
        ".jpg ", // Joint Photographic Experts Group", // a lossy image format widely used to display photographic images.
        ".jpeg", // Joint Photographic Experts Group", // a lossy image format widely used to display photographic images.
        ".jp2", // JPEG2000
        ".jps", // JPEG Stereo
        ".lbm", // Deluxe Paint image file
        ".max", // ScanSoft PaperPort document
        ".miff", // ImageMagick's native file format
        ".mng", // Multiple Network Graphics, the animated version of PNG
        ".msp", // a file format used by old versions of Microsoft Paint. Replaced with BMP in Microsoft Windows 3.0
        ".nitf", // A U.S. Government standard commonly used in Intelligence systems
        ".pbm", // Portable bitmap
        ".pc1", // Low resolution, compressed Degas picture file
        ".pc2", // Medium resolution, compressed Degas picture file
        ".pc3", // High resolution, compressed Degas picture file
        ".pcf", // Pixel Coordination Format
        ".pcx", // a lossless format used by ZSoft's PC Paint, popular at one time on DOS systems.
        ".pdn", // Paint.NET image file
        ".pgm", // Portable graymap
        ".pi1", // Low resolution, uncompressed Degas picture file
        ".pi2", // Medium resolution, uncompressed Degas picture file. Also Portrait Innovations encrypted image format.
        ".pi3", // High resolution, uncompressed Degas picture file
        ".pict", // Apple Macintosh PICT image
        ".pct", // Apple Macintosh PICT image
        ".png", // Portable Network Graphic (lossless, recommended for display and edition of graphic images)
        ".pnm", // Portable anymap graphic bitmap image
        ".pns", // PNS", // PNG Stereo
        ".ppm", // Portable Pixmap (Pixel Map) image
        ".psb", // Adobe Photoshop Big image file (for large files)
        ".psd", // Adobe Photoshop Drawing
        ".pdd", // Adobe Photoshop Drawing
        ".psp", // Paint Shop Pro image
        ".px", // Pixel image editor image file
        ".pxr", // Pixar Image Computer image file
        ".qfx", // QuickLink Fax image
        ".raw", // General term for minimally processed image data (acquired by a digital camera)
        ".rle", // a run-length encoded image
        ".sct", // Scitex Continuous Tone image file
        ".sgi",
        ".rgb", // Silicon Graphics Image
        ".int",
        ".bw",
        ".tga",
        ".targa", // Truevision TGA (Targa) image
        ".icb",
        ".vda",
        ".vst",
        ".pix",
        ".tif", // Tagged Image File Format (usually lossless, but many variants exist, including lossy ones)
        ".tiff", // ISO 12234-2; tends to be used as a basis for other formats rather than in its own right.
        ".vtf", // Valve Texture Format
        ".xbm", // X Window System Bitmap
        ".xcf", // GIMP image (from Gimp's origin at the eXperimental Computing Facility of the University of California)
        ".xpm", // X Window System Pixmap
        
        //  Vector graphics
        ".amf", // Additive Manufacturing File Format
        ".awg", // Ability Draw
        ".3dv", // 3-D wireframe graphics by Oscar Garcia
        ".ai", // Adobe Illustrator Document
        ".eps", // Encapsulated Postscript
        ".cgm", // Computer Graphics Metafile an ISO Standard
        ".cdr", // CorelDRAW Document
        ".cmx", // CorelDRAW vector image
        ".dxf", // ASCII Drawing Interchange file Format, used in AutoCAD and other CAD-programs
        ".e2d", // 2-dimensional vector graphics used by the editor which is included in JFire
        ".egt", // EGT Universal Document, EGT Vector Draw images are used to draw vector to a website
        ".odg", // OpenDocument Drawing
        ".svg", // Scalable Vector Graphics, employs XML
        ".stl", // Stereo Lithographic data format (see STL (file format)) used by various CAD systems and stereo lithographic printing machines. See above.
        ".vrml", // Virtual Reality Modeling Language, for the creation of 3D viewable web images.
        ".wrl",
        ".x3d",
        ".sxd", // OpenOffice.org XML (obsolete) Drawing
        ".v2d", // voucher design used by the voucher management included in JFire
        ".wmf", // Windows Meta File
        ".emf", // Enhanced (Windows) MetaFile, an extension to WMF
        ".art", // Xara", // Drawing (superseded by XAR)
        ".xar", // Xara", // Drawing
        
        //  3D graphics
        ".3dmf", // QuickDraw 3D Metafile (.3dmf)
        ".3dm", // OpenNURBS Initiative 3D Model (used by Rhinoceros 3D) (.3dm)
        ".3ds", // Legacy 3D Studio Model (.3ds)
        ".ac", // AC3D Model (.ac)
        ".amf", // Additive Manufacturing File Format
        ".an8", // Anim8or Model (.an8)
        ".aoi", // Art of Illusion Model (.aoi)
        ".b3d", // Blitz3D Model (.b3d)
        ".blend", // Blender (.blend)
        ".block", // Blender encrypted blend files (.block)
        ".c4d", // Cinema 4D (.c4d)
        ".cal3d", // Cal3D (.cal3d)
        ".ccp4", // X-ray crystallography voxels (electron density)
        ".cfl", // Compressed File Library (.cfl)
        ".cob", // Caligari Object (.cob)
        ".core3d", // Coreona 3D Coreona 3D Virtual File(.core3d)
        ".ctm", // OpenCTM (.ctm)
        ".dae", // COLLADA (.dae)
        ".dff", // RenderWare binary stream, commonly used by Grand Theft Auto III-era games as well as other RenderWare titles
        ".dpm", // deepMesh (.dpm)
        ".dts", // Torque Game Engine (.dts)
        ".egg", // Panda3D Engine
        ".fact", // Electric Image (.fac)
        ".fbx", // Autodesk FBX (.fbx)
        ".g", // BRL-CAD geometry (.g)
        ".glm", // Ghoul Mesh (.glm)
        ".jas", // Cheetah 3D file (.jas)
        ".lwo", // Lightwave Object (.lwo)
        ".lws", // Lightwave Scene (.lws)
        ".lxo", // Luxology Modo (software) file (.lxo)
        ".ma", // Autodesk Maya ASCII File (.ma)
        ".max", // Autodesk 3D Studio Max file (.max)
        ".mb", // Autodesk Maya Binary File (.mb)
        ".md2", // Quake 2 model format (.md2)
        ".md3", // Quake 3 model format (.md3)
        ".mdx", // Blizzard Entertainment's own model format (.mdx)
        ".mesh", // New York University(.m)
        ".mesh", // Meshwork Model (.mesh)
        ".mm3d", // Misfit Model 3d (.mm3d)
        ".mpo", // Multi-Picture Object", // This JPEG standard is used for 3d images, as with the Nintendo 3DS
        ".mrc", // voxels in cryo-electron microscopy
        ".nif", // Gamebryo NetImmerse File (.nif)
        ".obj", // Wavefront .obj file (.obj)
        ".off", // OFF Object file format (.off)
        ".prc", // Adobe PRC (embedded in PDF files)
        ".pov", // POV-Ray document (.pov)
        ".rwx", // RenderWare Object (.rwx)
        ".sia", // Nevercenter Silo Object (.sia)
        ".sib", // Nevercenter Silo Object (.sib)
        ".skp", // Google Sketchup file (.skp)
        ".sldasm", // SolidWorks Assembly Document (.sldasm)
        ".sldprt", // SolidWorks Part Document (.sldprt)
        ".smd", // Valve Studiomdl Data format. (.smd)
        ".u3d", // Universal 3D file format (.u3d)
        ".vue", // Vue scene file (.vue)
        ".wings", // Wings3D (.wings)
        ".x", // DirectX 3D Model (.x)
        ".x3d", // Extensible 3D (.x3d)
        ".z3d", // Zmodeler (.z3d)
        
        //  Mathematical
        //".mml", // MathML", // Mathematical Markup Language
        ".odf", // OpenDocument Math Formula
        ".sxm", // OpenOffice.org XML (obsolete) Math Formula
        
        //  Object code, executable files, shared and dynamically-linked libraries
        ".8bf", // plugins for some photo editing programs including Adobe Photoshop, Paint Shop Pro, GIMP and Helicon Filter.
        ".a", // static library
        ".a.out", // (no suffix for executable image, .o for object files, .so for shared object files) classic UNIX object format, now often superseded by ELF
        ".app", // apple application program executable file. Another form of zip file.
        ".bac", // an executable image for the RSTS/E system, created using the BASIC-PLUS COMPILE command[1]
        ".bpl", // a Win32 PE file created with Borland Delphi or C++Builder containing a package.
        ".bundle", // a Macintosh plugin created with Xcode or make which holds executable code, data files, and folders for that code.
        ".class", // used in Java
        ".o", // UNIX Common Object File Format, now often superseded by ELF
        ".com", // commands used in DOS
        ".dcu", // Delphi compiled unit
        ".dol", // the file format used by the Gamecube and Wii, short for Dolphin the codename of the Gamecube.
        ".ear", // archives of Java enterprise applications
        ".elf", // (no suffix for executable image, .o for object files, .so for shared object files) used in many modern Unix and Unix-like systems, including Solaris, other System V Release 4 derivatives, Linux, and BSD)
        ".dos", // used in DOS)
        ".jar", // archives of Java class files
        ".xpi", // PKZIP archive that can be run by Mozilla web browsers to install software)
        ".mach-o", // (no suffix for executable image, .o for object files, .dylib and .bundle for shared object files) Mach based systems, notably native format of Mac OS X)
        ".nlm", // the native 32-bit binaries compiled for Novell's NetWare Operating System (versions 3 and newer)
        ".exe", // used in DOS 4.0 and later, 16-bit Microsoft Windows, and OS/2)
        ".o", // un-linked object files directly from the compiler.
        ".dll", // used in Microsoft Windows and some other systems)
        ".s1es", // Executable used for S1ES learning system.
        ".so", // shared library, typically ELF
        ".vap", // the native 16-bit binaries compiled for Novell's NetWare Operating System (version 2, NetWare 286, Advanced NetWare, etc.)
        ".war", // archives of Java Web applications
        ".xbe", // Xbox executable
        ".xcoff", // (no suffix for executable image, .o for object files, .a for shared object files) extended COFF, used in AIX
        ".xex", // Xbox 360 executable
        
        //Object extensions
        ".vbx", // Visual Basic extensions
        ".ocx", // Object Control extensions
        ".tlb", // Windows Type Library
        
        //  Page description language
        ".dvi",
        ".egt", // Universal Document can be used to store CSS type styles (*.egt)
        ".pld",
        ".pcl",
        ".pdf", // Portable Document Format
        ".ps",
        ".snp", // Microsoft Access Report Snapshot
        ".xps",
        ".css", // Cascading Style Sheets
        ".xslt, xsl", // XML Style Sheet (.xslt, .xsl)
        ".tpl", // Web template (.tpl)
        
        //  Personal information manager
        ".msg", // Microsoft Outlook task manager
        ".org", // Lotus Organizer PIM package
        ".pst", // Microsoft Outlook email communication
        ".sc2", // Microsoft Schedule+ calendar
        
        //  Presentation
        ".key", // Apple Keynote Presentation
        ".keynote",
        ".nb", // Mathematica Slideshow
        ".nbp", // Mathematica Player slideshow
        ".odp", // OpenDocument Presentation
        ".otp", // OpenDocument Presentation template
        ".pot", // Microsoft PowerPoint template
        ".pps", // Microsoft PowerPoint Show
        ".ppt", // Microsoft PowerPoint Presentation
        ".pptx", // Office Open XML Presentation
        ".prz", // Lotus Freelance Graphics
        ".sdd", // StarOffice's StarImpress
        ".shf", // ThinkFree Show
        ".show", // Haansoft(Hancom) Presentation software document
        ".shw", // Corel Presentations slide show creation
        ".slp", // Logix-4D Manager Show Control Project
        ".sspss", // SongShow Plus Slide Show
        ".sti", // OpenOffice.org XML (obsolete) Presentation template
        ".sxi", // OpenOffice.org XML (obsolete) Presentation
        ".watch", // Dataton Watchout Presentation
        
        //  Project management software
        ".mpp", // Microsoft Project
        
        //  Reference management software
        ".bib", // BibTeX
        ".enl", // EndNote
        ".ris", // Research Information Systems RIS (file format)
        
        //  Scientific data (data exchange)
        ".silo", // a storage format for visualization developed at Lawrence Livermore National Laboratory
        ".spc", // spectroscopic data
        ".eas3", // binary file format for structured data
        ".ccp4", // X-ray crystallography voxels (electron density)
        ".mrc", // voxels in cryo-electron microscopy
        
        //  Multi-domain
        ".netcdf", // Network common data format
        ".hdr", // Hierarchical Data Format
        ".sdxf", // (Structured Data Exchange Format)
        ".cdf", // Common Data Format
        ".cgns", // CFD General Notation System
        
        //  Meteorology
        ".grib", // Grid In Binary, WMO format for weather model data
        ".bufr", // WMO format for weather observation data
        ".pp", // UK Met Office format for weather model data
        
        //  Biology
        ".ab1", // In DNA sequencing, chromatogram files used by instruments from Applied Biosystems
        ".ace", // A sequence assembly format
        ".bam", // Binary compressed SAM format
        ".caf", // Common Assembly Format for sequence assembly
        ".embl", // The flatfile format used by the EMBL to represent database records for nucleotide and peptide sequences from EMBL databases
        ".fasta", // The FASTA file format, for sequence data. Sometimes also given as FNA or FAA (Fasta Nucleic Acid or Fasta Amino Acid).
        ".fastq", // The FASTQ file format, for sequence data with quality. Sometimes also given as QUAL.
        ".genbank", // The flatfile format used by the NCBI to represent database records for nucleotide and peptide sequences from the GenBank and RefSeq databases
        ".gff", // The General feature format is used for describing genes and other features of DNA, RNA and protein sequences
        ".gtf", // The Gene transfer format is used to hold information about gene structure.
        ".pdb", // structures of biomolecules deposited in Protein Data Bank. Also used for exchanging protein/nucleic acid structures.
        ".phd", // Phred output, from the basecalling software Phred
        ".sam", // Sequence Alignment/Map format, in which the results of the 1000 Genomes Project will be released.
        ".scf", // Staden chromatogram files used to store data from DNA sequencing
        ".sbml", // The Systems Biology Markup Language is use to store biochemical network computational models
        ".stockholm", // The Stockholm format for representing multiple sequence alignments
        ".swiss-prot", // The flatfile format used to represent database records for protein sequences from the Swiss-Prot database
        ".vcf", // Variant Call Format, a standard created by the 1000 Genomes Project that lists and annotates the entire collection of human variants (with the exception of approximately 1.6 million variants).
        
        //  Biomedical imaging
        ".nii", // single-file (combined data and meta-data) style
        ".nii.gz", // gzip-compressed, used transparently by some software, notably the FMRIB Software Library (FSL)
        ".gii", // single-file (combined data and meta-data) style; NIfTI offspring for brain surface data
        ".mgh", // uncompressed
        ".mgz", // zip-compressed
        
        //  Biomedical signals (time series)
        ".acq", // AcqKnowledge File Format for Windows/PC from Biopac Systems Inc., Goleta, CA, USA.
        ".bci2000", // The BCI2000 project, Albany, NY, USA.
        ".bdf", // BioSemi data format from BioSemi B.V. Amsterdam, Netherlands.
        ".bkr", // The EEG data format developed at the University of Technology Graz, Austria.
        ".cfwb", // Chart Data File Format from ADInstruments Pty Ltd, Bella Vista NSW, Australia.
        ".dicom", // Waveform An extension of Dicom for storing waveform data
        ".ecgml", // A markup language for electrocardiogram data acquisition and analysis.
        ".edf/edf+", // European Data Format.
        ".fef", // File Exchange Format for Vital signs, CEN TS 14271.
        ".gdf", // The General Data Format for biomedical signals", // Version 1.x.
        ".hl7aecg", // Health Level 7 v3 annotated ECG.
        ".mfer", // Medical waveform Format Encoding Rules
        ".openxdf", // Open Exchange Data Format from Neurotronics, Inc. Gainesville, FL, USA.
        ".scp-ecg", // Standard Communication Protocol for Computer assisted electrocardiography EN1064:2007,
        ".sigif", // A digital SIGnal Interchange Format with application in neurophysiology.
        ".wfdb", // Format of Physiobank
        
        //  Script -- assume these are all text
        /*
        ".ahk", // AutoHotkey script file
        ".applescript", // See SCPT.
        ".as", // Adobe Flash ActionScript File
        ".au3", // AutoIt version 3
        ".bat", // Batch file
        ".bas", // QBasic & QuickBASIC
        ".cmd", // Batch file
        ".coffee", // CoffeeScript
        ".egg", // Chicken
        ".egt", // EGT Asterisk Application Source File, EGT Universal Document
        ".erb", // Embedded Ruby
        ".hta", // HTML Application
        ".ibi", // Icarus script
        ".ici", // ICI
        ".itcl", // Itcl
        ".js", // JavaScript and JScript
        ".jsfl", // Adobe JavaScript language
        ".lua", // Lua
        ".m", // Mathematica package file
        ".mrc", // mIRC Script
        ".ncf", // NetWare Command File (scripting for Novell's NetWare OS)
        ".nut", // Squirrel
        ".php", // PHP
        ".php?", // PHP (? = version number)
        ".pl", // Perl
        ".pm", // Perl module
        ".ps1", // Windows PowerShell shell script
        ".ps1xml", // Windows PowerShell format and type definitions
        ".psc1", // Windows PowerShell console file
        ".psd1", // Windows PowerShell data file
        ".psm1", // Windows PowerShell module file
        ".py", // Python
        ".pyc", // Python
        ".pyo", // Python
        ".r", // R scripts
        ".rb", // Ruby
        ".rdp", // RDP connection
        ".scpt", // Applescript
        ".scptd", // See SCPT.
        ".sdl", // State Description Language
        ".sh", // Shell script
        ".tcl", // Tcl
        ".vbs", // Visual Basic Script
        ".xpl", // XProc script/pipeline
        ".ebuild", // Gentoo linux's portage package.
        */
        
        //  Signal data (non-audio)
        ".acq", // AcqKnowledge File Format for Windows/PC from Biopac
        ".bkr", // The EEG data format developed at the University of Technology Graz
        ".bdf", // BioSemi data format", // similar to EDF but 24bit
        ".cfwb", // Chart Data File Format from ADInstruments
        ".edf", // European data format
        ".fef", // File Exchange Format for Vital signs
        ".gdf", // General data formats for biomedical signals
        ".gms", // Gesture And Motion Signal format
        ".irock", // intelliRock Sensor Data File Format
        ".mfer", // Medical waveform Format Encoding Rules
        ".scp-ecg", // Standard Communication Protocol for Computer assisted electrocardiography
        ".seg y", // Reflection seismology data format
        ".sigif", // SIGnal Interchange Format
        
        //  Sound and music
        ".aiff", // Audio Interchange File Format
        ".au",
        ".cdda",
        ".iff-8svx",
        ".iff-16sv",
        ".raw", // raw samples without any header or sync
        ".wav", // Microsoft Wave
        ".flac", // (free lossless codec of the Ogg project)
        ".la", // Lossless Audio (.la)
        ".pac", // LPAC (.pac)
        ".m4a", // Apple Lossless (M4A)
        ".ape", // Monkey's Audio (APE)
        ".rka", // RKAU (.rka)
        ".shn", // Shorten (SHN)
        ".tta", // free lossless audio codec (True Audio)
        ".wv", // WavPack (.wv)
        ".wma", // Windows Media Audio 9 Lossless (WMA)
        
        //  Lossy audio
        ".amr", // for GSM and UMTS based mobile phones
        ".mp2", // MPEG Layer 2
        ".mp3", // MPEG Layer 3
        ".speex", // Ogg project, specialized for voice, low bitrates
        ".gsm", // GSM Full Rate, originally developed for use in mobile phones
        ".wma", // Windows Media Audio (.WMA)
        ".m4a, .mp4, .m4p, .aac", // Advanced Audio Coding (usually in an MPEG-4 container)
        ".mpc", // Musepack
        ".vqf", // Yamaha TwinVQ
        ".ra", // Real Audio
        ".rm", // Real Audio
        ".ots", // Audio File (similar to MP3, with more data stored in the file and slightly better compression; designed for use with OtsLabs' OtsAV)
        ".swa", // Macromedia Shockwave Audio (Same compression as MP3 with additional header information specific to Macromedia Director
        ".vox", // Dialogic ADPCM Low Sample Rate Digitized Voice (VOX)
        ".voc", // Creative Labs Soundblaster Creative Voice 8-bit & 16-bit (VOC)
        ".dwd", // DiamondWare Digitized (DWD)
        ".smp", // Turtlebeach SampleVision (SMP)
        
        //  Other music
        ".aup", // Audacity project file
        ".band", // GarageBand music
        ".cust", // DeliPlayer custom sound file format
        ".mid", // standard MIDI file; most often just notes and controls but occasionally also sample dumps
        ".mus", // Finale Notation file, see also Finale (software)
        ".sib", // Sibelius Notation file, see also Sibelius (computer program)
        ".ly", // LilyPond Notation file, see also GNU LilyPond
        ".gym", // Sega Genesis YM2612 log
        ".vgm", // stands for "video game music", log for several different chips
        ".psf",
        ".nsf", // NES Sound Format, bytecode program to play NES music
        ".mod", // Soundtracker and Protracker sample and melody modules
        ".ptb", // Power Tab Editor tab
        ".s3m", // Scream Tracker 3 module, with a few more effects and a dedicated volume column
        ".xm", // Fast Tracker module, adding instrument envelopes
        ".it", // Impulse Tracker module, adding compressed samples, note-release actions, and more effects including a resonant filter
        ".mt2", // MadTracker 2 module. It could be resumed as being XM and IT combined with more features like track effects and automation.
        ".mng", // BGM for the Creatures game series, starting from Creatures 2
        ".psf", // Portable Sound Format, PlayStation variant (originally PlayStation Sound Format).
        ".minipsf", // Multipart PSF
        ".psflib", // Multipart PSF
        ".2sf", // PSF for other platforms
        ".dsf",
        ".gsf",
        ".psf2",
        ".qsf",
        ".ssf",
        ".usf",
        ".rmj", // RealJukebox Media used for RealPlayer.
        ".spc", // Super Nintendo Entertainment System sound file format.
        ".txm", // Track ax media
        ".ym", // Atari ST/Amstrad CPC YM2149 sound chip format
        ".jam", // Jam music format
        ".asf", // Advanced Systems Format
        ".mp1", // for use with UltraPlayer
        ".mscz", // Musescore compressed file
        ".mscz,", // Musescore uncompressed file
        
        //  Playlists
        ".asx", // Advanced Stream Redirector (.asx)
        ".m3u",
        ".pls",
        ".ram", // Real Audio Metafile For Real Audio files only.
        //".txt", // Mplayer playlist
        ".xpl", // HDi playlist
        ".xspf", // the XML Shareable Playlist Format
        ".zpl", // Zune Playlist format
        
        //  Audio editing, music production
        ".als", // Ableton Live set
        ".aup", // Audacity project file
        ".band", // GarageBand project file
        ".cel", // Adobe Audition loop file (Cool Edit Loop)
        ".cpr", // Steinberg Cubase project file
        ".mmr", // MAGIX Music Maker project file
        ".npr", // Steinberg Nuendo project file
        ".cwp", // Cakewalk Sonar project file
        ".drm", // Steinberg Cubase drum file
        ".omf", // cross-application format Open Media Framework application-exchange bundled format
        ".ses", // Adobe Audition multitrack session file
        ".sfl", // Sound Forge sound file
        ".sng", // MIDI sequence file (MidiSoft, Korg, etc.) or n-Track Studio project file
        ".stf", // StudioFactory project file. It contains all necessary patches, samples, tracks and settings to play the file.
        ".syn", // SynFactory project file. It contains all necessary patches, samples, tracks and settings to play the file.
        ".snd", // Akai MPC sound file
        
        //  Source code for computer programs -- assume these are all text
        /*
        ".ada, adb, 2.ada", // Ada (body) source
        ".ads, 1.ada", // Ada (specification) source
        ".asm, s", // Assembly language source
        ".bas", // BASIC, Visual Basic, BASIC-PLUS source[1]
        ".bb", // Blitz3D
        ".bmx", // BlitzMax
        ".c", // C source
        ".clj", // Clojure source code
        ".cls", // Visual Basic class
        ".cob, cbl", // COBOL source
        ".cpp, cc, cxx, c", // C++ source
        ".cs", // C# source
        ".csproj", // C# project (Visual Studio .NET)
        ".d", // D source
        ".dba", // DarkBASIC source
        ".dbpro", // DarkBASIC Professional project
        ".e", // Eiffel source
        ".efs", // EGT Forever Source File
        ".egt", // EGT Asterisk Source File, could be J, C#, VB.net, EF 2.0 (EGT Forever)
        ".el", // Emacs Lisp source
        ".for, ftn, f, f77, f90", // Fortran source
        ".frm", // Visual Basic form
        ".frx", // Visual Basic form stash file (binary form file)
        ".ged", // Game Maker Extension Editable file as of version 7.0
        ".gm6", // Game Maker Editable file as of version 6.x
        ".gmd", // Game Maker Editable file up to version 5.x
        ".gmk", // Game Maker Editable file as of version 7.0
        ".gml", // Game Maker Language script file
        ".go", // Go source
        ".h", // C/C++ header file
        ".hpp, hxx", // C++ header file
        ".hs", // Haskell source
        ".inc", // Turbo Pascal included source
        ".java", // Java source
        ".l", // lex source
        ".lisp", // Common Lisp source
        ".m", // Objective-C source
        ".m", // MATLAB
        ".m", // Mathematica
        ".m4", // m4 source
        ".ml", // Standard ML / Objective Caml source
        ".n", // Nemerle source
        ".nb", // Nuclear Basic source
        ".pas, pp, p", // Pascal source (DPR for projects)
        ".p", // Parser source
        ".php, php3, php4, php5, phps, phtml", // PHP source
        ".piv", // Pivot stickfigure animator
        ".pl, pm", // Perl
        ".prg", // db, clipper, Microsoft FoxPro, harbour and Xbase
        ".py", // Python source
        ".rb", // Ruby source
        ".resx", // Resource file for .NET applications
        ".rc, rc2", // Resource script files to generate resources for .NET applications
        ".rkt, rktl", // Racket source
        ".sci, sce", // Scilab
        ".scm", // Scheme source
        ".skb, skc", // Sage Retrieve 4GL Common Area (Main and Amended backup)
        ".skd", // Sage Retrieve 4GL Database
        ".skf, skg", // Sage Retrieve 4GL File Layouts (Main and Amended backup)
        ".ski", // Sage Retrieve 4GL Instructions
        ".skk", // Sage Retrieve 4GL Report Generator
        ".skm", // Sage Retrieve 4GL Menu
        ".sko", // Sage Retrieve 4GL Program
        ".skp, skq", // Sage Retrieve 4GL Print Layouts (Main and Amended backup)
        ".sks, skt", // Sage Retrieve 4GL Screen Layouts (Main and Amended backup)
        ".skz", // Sage Retrieve 4GL Security File
        ".sln", // Visual Studio solution
        ".spin", // Spin source (for Parallax Propeller microcontrollers)
        ".stk", // Stickfigure file for Pivot stickfigure animator
        ".vap", // Visual Studio Analyzer project
        ".vb", // Visual Basic.NET source
        ".vbp, vip", // Visual Basic project
        ".vbg", // Visual Studio compatible project group
        ".vbproj", // Visual Basic .NET project
        ".vcproj", // Visual C++ project
        ".vdproj", // Visual Studio deployment project
        ".xpl", // XProc script/pipeline
        ".xq", // XQuery file
        ".xsl", // XSLT stylesheet
        ".y", // yacc source
        */
        
        //  Spreadsheet
        ".123", // Lotus 1-2-3
        ".ab2", // Abykus worksheet
        ".ab3", // Abykus workbook
        ".aws", // Ability Spreadsheet
        ".clf", // ThinkFree Calc
        ".cell", // Haansoft(Hancom) SpreadSheet software document
        //".csv", // Comma-Separated Values
        ".numbers", // An Apple Numbers Spreadsheet file
        ".gnumeric", // Gnumeric spreadsheet, a gziped XML file
        ".ods", // OpenDocument spreadsheet
        ".ots", // OpenDocument spreadsheet template
        ".qpw", // Quattro Pro spreadsheet
        ".sdc", // StarOffice StarCalc Spreadsheet
        ".slk", // SYLK (SYmbolic LinK)
        ".stc", // OpenOffice.org XML (obsolete) Spreadsheet template
        ".sxc", // OpenOffice.org XML (obsolete) Spreadsheet
        ".tab", // tab delimited columns; also TSV (Tab-Separated Values)
        ".txt", // tab delimited columns
        ".vc", // Visicalc
        ".wk1", // Lotus 1-2-3 up to version 2.01
        ".wk3", // Lotus 1-2-3 version 3.0
        ".wk4", // Lotus 1-2-3 version 4.0
        ".wks", // Lotus 1-2-3
        ".wks", // Microsoft Works
        ".wq1", // Quattro Pro DOS version
        ".xlk", // Microsoft Excel worksheet backup
        ".xls", // Microsoft Excel worksheet sheet (97–2003)
        ".xlsb", // Microsoft Excel binary workbook
        ".xlsm", // Microsoft Excel Macro-enabled workbook
        ".xlsx", // Office Open XML worksheet sheet
        ".xlr", // Microsoft Works version 6.0
        ".xlt", // Microsoft Excel worksheet template
        ".xltm", // Microsoft Excel Macro-enabled worksheet template
        ".xlw", // Microsoft Excel worksheet workspace (version 4.0)
        
        //  Tabulated data
        //".tsv", // Tab-separated values
        //".csv", // Comma-separated values
        ".db", // databank format; accessible by many econometric applications
        ".dif", // accessible by many spreadsheet applications
        
        //  Video
        ".aaf", // mostly intended to hold edit decisions and rendering information, but can also contain compressed media essence
        ".3gp", // the most common video format for cell phones
        ".gif", // Animated GIF (simple animation; until recently often avoided because of patent problems)
        ".asf", // container (enables any form of compression to be used; MPEG-4 is common; video in ASF-containers is also called Windows Media Video (WMV))
        ".avchd", // Advanced Video Codec High Definition
        ".avi", // container (a shell, which enables any form of compression to be used)
        ".cam", // aMSN webcam log file
        ".dat", // video standard data file (automatically created when we attempted to burn as video file on the CD)
        ".dsh",
        ".flv", // Flash video (encoded to run in a flash animation)
        ".m1v", // Video
        ".m2v", // Video
        ".fla", // Macromedia Flash (for producing)
        ".flr", // (text file which contains scripts extracted from SWF by a free ActionScript decompiler named FLARE)
        ".sol", // Adobe Flash shared object ("flash cookie")
        ".m4v", // (file format for videos for iPods and PlayStation Portables developed by Apple)
        ".mkv", // Matroska is a container format, which enables any video format such as MPEG-4 ASP or AVC to be used along with other content such as subtitles and detailed meta information
        ".wrap", // MediaForge (*.wrap)
        ".mng", // mainly simple animation containing PNG and JPEG objects, often somewhat more complex than animated GIF
        ".mov", // container which enables any form of compression to be used; Sorenson codec is the most common; QTCH is the filetype for cached video and audio streams
        ".mpeg",
        ".mpg",
        ".mpe",
        ".mp4", // multimedia container (most often used for Sony's PlayStation Portable and Apple's iPod)
        ".mxf", // Material Exchange Format (standardized wrapper format for audio/visual material developed by SMPTE)
        ".roq", // used by Quake 3
        ".nsv", // Nullsoft Streaming Video (media container designed for streaming video content over the Internet)
        ".ogg", // container, multimedia
        ".rm", // RealMedia
        ".svi", // Samsung video format for portable players
        ".smi", // SAMI Caption file (HTML like subtitle for movie files)
        ".swf", // Macromedia Flash (for viewing)
        ".wmv", // Windows Media Video (See ASF)
        
        //  Video editing, production
        ".fcp", // Final Cut Pro project file
        ".mswmm", // Windows Movie Maker project file
        ".ppj", // Adobe Premiere Pro video editing file
        ".imovieproj", // iMovie project file
        ".veg",
        ".veg-bak", // Sony Vegas project file
        ".suf", // Sony camera configuration file (setup.suf) produced by XDCAM-EX camcorders
        
        //  Video game data
        // File formats used by games based on the Halo engine.
        ".map", // A Level, User Interface, or Sounds
        ".tag", // An Object
        ".sav", // A saved game
        ".lev", // A HALO ZERO Level
        // File formats used by games based on the TrackMania engine.
        ".challenge.gbx", // (Edited) Challenge files.
        ".constructioncampaign.gbx", // Construction campaignes files.
        ".controleffectmaster.gbx/controlstyle.gbx", // Menu parts.
        ".fidcache.gbx", // Saved game.
        ".gbx", // Other TrackMania items.
        ".replay.gbx", // Replays of races.
        // File formats used by games based on the DOOM engine.
        ".deh", // DeHackEd files to mutate the game executable (not officially part of the DOOM engine)
        ".dsg", // Saved game
        ".lmp", // A lump is an entry in a DOOM wad.
        ".lmp", // Saved demo recording
        ".mus", // Music file (usually contained within a WAD file)
        ".wad", // Data storage (contains music, maps, and textures)
        // File formats used by games based on the Quake engine.
        ".bsp", // (For Binary space partitioning) compiled map format
        ".map", // Raw map format used by editors like GtkRadiant or QuArK
        ".mdl", // Model for an item used in the game
        ".md2",
        ".md3",
        ".md5",
        ".pak", // Data storage
        ".pk2", // used by the Quake II, Quake III Arena and Quake 4 game engines
        ".pk3",
        ".pk4",
        ".dat", // general data contained within the .PK3/PK4 files
        ".fontdat", // a .dat file used for formatting game fonts
        ".roq", // Video format
        // File formats used by the Unreal Engine
        ".u", // Unreal script format
        ".uax", // Animations format for Unreal Engine 2
        ".umx", // Map format for Unreal Tournament
        ".umx", // Music format for Unreal Engine 1
        ".unr", // Map format for Unreal
        ".upk", // Package format for cooked content in Unreal Engine 3
        ".usx", // Sound format for Unreal Engine 1 and Unreal Engine 2
        ".ut2", // Map format for Unreal Tournament 2003 and Unreal Tournament 2004
        ".ut3", // Map format for Unreal Tournament 3
        ".utx", // Texture format for Unreal Engine 1 and Unreal Engine 2
        ".uxx", // Cache format. These are files that client downloaded from server (which can be converted to regular formats)
        ".dmo", // Save game
        ".grp", // Data storage
        ".map", // Map (usually constructed with BUILD.EXE)
        // File formats used by Diablo by Blizzard Entertainment.
        ".sv", // Save Game
        ".itm", // Item File
        // File formats used by Bohemia Interactive. Operation:Flashpoint, ARMA 2, VBS2
        ".sqf", // Format used for general editing
        ".sqm", // Format used for mission files
        ".pbo", // Binarized file used for compiled models
        ".lip", // Format that is created from WAV files to create in-game accurate lip-synch for character animations.
        //    Other Formats
        ".b", // used for Grand Theft Auto saved game files
        ".bol", // used for levels on Poing!PC
        ".dbpf", // The Sims 2, DBPF, Package
        ".he0",
        ".he2",
        ".he4",
        ".he",
        ".gcf", // format used by the Steam content management system for file archives.
        ".img", // format used by Renderware-based Grand Theft Auto games for data storage
        ".map", // format used by Halo: Combat Evolved for archive compression, Doom³, and various other games
        ".oec", // format used by OE-Cake for scene data storage.
        ".p3d", // format for panda3d by disney.
        ".pod", // format used by Terminal Reality
        ".rep", // used by Blizzard Entertainment for scenario replays in StarCraft.
        ".dat", // All game plugins use this format, commonly with different file extensions
        ".sc4lot", // All game plugins use this format, commonly with different file extensions
        ".sc4model", // All game plugins use this format, commonly with different file extensions
        ".smzip", // ZIP-based package for Stepmania songs, themes and announcer packs.
        
        //  Video game storage media
        ".jag", // Atari Jaguar (.jag, .j64)
        ".j64",
        ".nds", // Nintendo DS (.nds)
        ".gb", // Game Boy (.gb) (this applies to the original Game Boy and the Game Boy Color)
        ".gbc", // Game Boy Color (.gbc)
        ".gba", // Game Boy Advance (.gba)
        ".gba", // Game Boy Advance (.gba)
        ".sav", // Game Boy Advance Saved Data Files (.sav)
        ".sgm", // Visual Boy Advance Save States (.sgm)
        ".n64", // Nintendo 64 (.n64, .v64, .z64, .u64, .usa, .jap, .pal, .eur, .bin)
        ".v64",
        ".z64",
        ".u64",
        ".usa",
        ".jap",
        ".pal",
        ".eur",
        ".pj", // Project 64 Save States (.pj)
        ".nes", // Nintendo Entertainment System (.nes)
        ".fds", // Famicom Disk System (.fds)
        ".jst", // Jnes Save States (.jst)
        //".fc?", // FCEUX Save States (.fc#, where # is any character, usually a number)
        ".gg", // Sega Game Gear (.gg)
        ".sms", // Sega Master System (.sms)
        ".smd", // Mega Drive/Sega Genesis (.smd or .bin)
        ".smc", // Super NES (.smc, or .078) (.078 is for split ROMs, which are rare)
        ".078",
        ".fig", // Super Famicom (Japanese releases are rarely .fig, above extensions are more common)
        ".srm", // Super NES Saved Data Files (.srm)
        ".zst", // ZSNES Save States (.zst, .zs1-.zs9, .z10-.z99)
        ".frz", // Snes9X Save States (.frz, .000-.008)
        ".pce", // TurboGrafx-16/PC Engine (.pce)
        ".npc", // Neo Geo Pocket (.npc)
        ".tzx", // ZX Spectrum (.tzx) (for exact copies of ZX Spectrum games)
        
        //        TAP (for tape images without copy protection)
        ".z80", // (for snapshots of the emulator RAM)
        ".sna",
        ".dsk", // (for disk images)
        ".tap", // Commodore 64 (.tap) (for tape images including copy protection)
        ".t64", // (for tape images without copy protection, considerably smaller than .tap files)
        ".d64", // (for disk images)
        ".crt", // (for cartridge images)
        ".adf", // Amiga (.adf) (for 880K diskette images)
        ".adz", // GZip-compressed version of the above.
        ".dms", // Disk Masher System, previously used as a disk-archiving system native to the Amiga, also supported by emulators.
        
        //  Virtual machines
        //  Microsoft Virtual PC, Virtual Server
        ".vfd", // Virtual Floppy Disk (.vfd)
        ".vhd", // Virtual Hard Disk (.vhd)
        ".vud", // Virtual Undo Disk (.vud)
        ".vmc", // Virtual Machine Configuration (.vmc)
        ".vsv", // Virtual Machine Saved State (.vsv)
        
        //  EMC VMware ESX, GSX, Workstation, Player
        ".log", // Virtual Machine Logfile (.log)
        ".vmdk", // Virtual Machine Disk (.vmdk, .dsk)
        ".dsk",
        ".nvram", // Virtual Machine BIOS (.nvram)
        ".vmem", // Virtual Machine paging file (.vmem)
        ".vmsd", // Virtual Machine snapshot metadata (.vmsd)
        ".vmsn", // Virtual Machine snapshot (.vmsn)
        ".vmss", // Virtual Machine suspended state (.vmss, .std)
        ".std",
        ".vmtm", // Virtual Machine team data (.vmtm)
        ".vmx", // Virtual Machine configuration (.vmx, .cfg)
        ".vmxf", // Virtual Machine team configuration (.vmxf)
        
        //  Virtualbox
        ".vdi", // VirtualBox Virtual Disk Image (.vdi)
        
        //  Parallels Workstation
        ".hdd", // Virtual Machine hard disk (.hdd)
        ".pvs", // Virtual Machine preferences/configuration (.pvs)
        ".sav", // Virtual Machine saved state (.sav)
        
        //  Webpage -- mostly text
        //".dtd", //Document Type Definition (standard), MUST be public and free
        //".rna", // lime Network Real Native Application File
        //".xml", // eXtensible Markup Language
        //".html", // HyperText Markup Language
        //".htm",
        //".xhtml", // eXtensible HyperText Markup Language
        //".xht",
        ".mht", // Archived HTML, store all data on one web page (text, images, etc.) in one big file
        ".mhtml",
        ".maff", // web archive based on ZIP
        //".asp", // Microsoft Active Server Page
        //".aspx", // Microsoft Active Server Page. NET
        //".adp", // AOLserver Dynamic Page
        //".bml", // Better Markup Language (templating)
        //".cfm", // ColdFusion
        //".cgi", // (.cgi)
        //".ihtml", // Inline HTML
        //".jsp", // (.jsp) JavaServer Pages
        //".lasso", // (.las, .lasso, .lassoapp)
        //".pl", // Perl (.pl)
        //".php", // ? is version number (previously abbreviated Personal Home Page, later changed to PHP: Hypertext Preprocessor)
        //".php?",
        //".phtml",
        //".shtml", // HTML with Server Side Includes (Apache)
        //".stm", // HTML with Server Side Includes (Apache)
        
        //  XML, markup language, other web standards-based file formats
        // assume text
        /*
        ".atom", // Another syndication file format
        ".xml",
        ".eml", // File format used by several desktop email clients
        ".metalink", // A file format for listing metadata about downloads, such as mirrors, checksums, and other information.
        ".met",
        ".rss", // Syndication file format
        */
        
        //  Other
        ".axd", // cookie extensions found in temporary internet folder
        ".axx", // encrypted file, created with Axcrypt
        ".bak", // backup file
        ".bdf", // Binary Data Format", // raw data from recovered blocks of unallocated space on a hard drive
        ".cbp", // CD Box Labeler Pro, CentraBuilder, Code::Blocks Project File, Conlab Project[2]
        ".credx", // CredX Dat File
        ".dupx", // DuupeCheck database management tool project file
        ".ga3", // Graphical Analysis 3
        ".ged", // GEDCOM, (GEnealogical Data COMmunication) file format for exchanging genealogical data between different genealogy software
        ".gxk", // Galaxkey, an encryption platform for authorized, private and confidential email communication
        ".hlp", // Windows help file
        ".igc", // flight tracks downloaded from GPS devices in the FAI's prescribed format
        //".ini", // used by many applications to store configuration
        //".inf", // similar file format to INI; used to install device drivers under Windows, inter alia.
        ".kmc", // tests made with KatzReview's MegaCrammer
        ".lnk", // Binary format file, stores shortcuts under MS Windows 95 and later
        ".lsm", // LSMaker script file (program using layered .jpg to create special effects; specifically designed to render lightsabers from the Star Wars universe) (.lsm)
        ".pif", // Used for running MS-DOS programs under Windows
        ".por", // So called "portable" SPSS files, readable by PSPP
        ".pxz", // Compressed file to exchange media elements with PSALMO
        ".rise", // File containing RISE generated information model evolution
        ".topc", // TopicCrunch SEO Project file holding keywords, domain and search engine settings (ASCII);
        ".tos", // Character file from The Only Sheet
        ".tmp", // Temporary file
        ".url", // INI format file, used by Internet Explorer to save Favorites
        ".xlf", // Extensible LADAR Format
        ".zed", // My Heritage Family Tree
        
        //  Cursors
        ".ani", // Animated Cursor
        ".cur", // Cursor Files
        
        //  Financial records
        ".myo", // MYOB Limited (Windows) File
        ".myob", // MYOB Limited (Mac) File
        ".nominal", // Nominal Accounting (CDF) Company Data File
        ".tax", // TurboTax File
        ".ynab" // You Need a Budget (YNAB) File
    };
    
    /**
     * Check if the given filename represents a known binary file type.
     * @param filename A file name with extension.
     * @return True if the file name matches any of the known binary file types, 
     * false otherwise. Note that a return value of "false" does not necessarily 
     * mean the file is not binary.
     */
    public static boolean isBinary(String filename) {
        if (filename == null || filename.length() == 0){
            throw new IllegalArgumentException("filename may not be null or empty");   
        }
        for (String ext : binary) {
            if (filename.toLowerCase().endsWith(ext)) {
                return true;   
            }
        }
        return false;
    }
    
    // Check if buffer contents are binary.
    static boolean isBinary(Buffer buffer) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer.getText().getBytes());
            return MiscUtilities.isBinary(bais);
        }
        catch(IOException e) {
            // shouldn't happen
            return false;
        }
    }
    
}