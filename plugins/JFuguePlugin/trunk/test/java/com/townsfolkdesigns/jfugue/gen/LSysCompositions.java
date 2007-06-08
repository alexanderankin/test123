package com.townsfolkdesigns.jfugue.gen;

import java.io.File;

import org.jfugue.Pattern;
import org.jfugue.Player;

public class LSysCompositions {
	public static void main(String[] args) {
		Composition composition = new XComposition();
		//composition = new KebuComposition();
		Pattern pattern = composition.getPattern();
		Player player = new Player();
		player.play(pattern);

		try {
			player.saveMidi(pattern, new File(composition.getTitle() + ".mid"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.exit(0);

	}
}

class KebuComposition implements Composition {
	public String getTitle() {
		return "kebu";
	}

	public Pattern getPattern() {
		MusicGenerator musicGenerator = new LSysMusicGenerator();
		String axiom = "T120 " + "V0 I[Flute] Rq C5q " + "V1 I[Tubular_Bells] Rq Rq Rq G6i+D6i "
		      + "V2 I[Piano] Cmajw E6q " + "V3 I[Warm] E6q G6i+D6i " + "V4 I[Voice] C5q E6q";
		musicGenerator.addTransform(new MusicTransform("Cmajw", "Cmajw Fmajw"));
		musicGenerator.addTransform(new MusicTransform("Fmajw", "Rw Bbmajw"));
		musicGenerator.addTransform(new MusicTransform("Bbmajw", "Rw Fmajw"));
		musicGenerator.addTransform(new MusicTransform("C5q", "C5q G5q E6q C6q"));
		musicGenerator.addTransform(new MusicTransform("E6q", "G6q D6q F6i C6i D6q"));
		musicGenerator.addTransform(new MusicTransform("G6i+D6i", "Rq Rq G6i+D6i G6i+D6i Rq"));

		String generatedMusic = musicGenerator.generate(axiom, 10);
		Pattern pattern = new Pattern(generatedMusic);
		return pattern;
	}
}

class XComposition implements Composition {
	public String getTitle() {
		return "misiba";
	}

	public Pattern getPattern() {
		MusicGenerator musicGenerator = new LSysMusicGenerator();
		// musicGenerator.setAxiom("T120 "
		// + "V1 I[SYNTHSTRINGS_1] C5q D5q E5i F5i G5q "
		// + "V2 I[FLUTE] B5i C5q E5i F5i G5q D4i "
		// + "V3 I[VOICE_OOHS] G5q B5q C5q B5q D5q "
		// + "V4 I[NEW_AGE] C5q B5q D5q G5q B5q");
		// musicGenerator.addTransform("G5q", "B5i D4i C5i D4i");
		// musicGenerator.addTransform("C5q", "G5s B5s C6s C6s");
		// musicGenerator.addTransform("D4i", "D4i B3i C5i");
		// musicGenerator.addTransform("B3i", "B3s B3s");
		// musicGenerator.addTransform("B5i", "B5i G5i D4i C5q");
		// musicGenerator.addTransform("D5q", "E5i B5i F5i B5i G5q ");

		String axiom = "T120 " + "V1 I[SYNTHSTRINGS_1] C5q D5q E5q F5q G5q "
		      + "V2 I[FLUTE] B5q C5q E5q F5q G5q D4q " + "V3 I[VOICE_OOHS] G5q B5q C5q B5q D5q "
		      + "V4 I[NEW_AGE] C5q B5q D5q G5q B5q";
		musicGenerator.addTransform(new MusicTransform("G5q", "B5q D4q C5q D4q"));
		musicGenerator.addTransform(new MusicTransform("C5q", "G5s B5s C6s C6s"));
		musicGenerator.addTransform(new MusicTransform("D4q", "D4q B3q C5q"));
		musicGenerator.addTransform(new MusicTransform("B3q", "B3i B3i"));
		musicGenerator.addTransform(new MusicTransform("B5q", "B5q G5q D4i C5q"));
		musicGenerator.addTransform(new MusicTransform("D5q", "E5q B5q F5q B5q G5q "));

		String generatedMusic = musicGenerator.generate(axiom, 10);
		System.out.println(generatedMusic);
		Pattern pattern = new Pattern(generatedMusic);
		return pattern;
	}
}