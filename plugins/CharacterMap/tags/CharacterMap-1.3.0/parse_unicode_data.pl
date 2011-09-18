#!/usr/bin/perl -w

use strict;

# We don't currently deal with Unicode chars above FFFF
use constant CUTOFF => 0xFFFF;
use constant CUTOFF_STRING => sprintf '0x%04X', CUTOFF;

use subs qw( parse_ucd_file dump_blocks dump_points );

my @blocks = ();
parse_ucd_file('zcat charactermap/unicode/Blocks.txt.gz |', sub {
	my ($range, $name) = @_[0,1];
	my ($first, $last) = split /\.\./, $range;
	return if hex($first) > CUTOFF;
	return if hex($last) > CUTOFF;
	push @blocks, [ $first, $last, $name ];
	1;
});
dump_blocks(@blocks);

my @points = ();
parse_ucd_file('zcat charactermap/unicode/UnicodeData.txt.gz |', sub {
	my ($point, $name, $oldname) = @_[0,1,10];
	$name .= " ($oldname)" if $name eq '<control>' and $oldname;
	return if hex($point) > CUTOFF;
	push @points, [ $point, $name ];
	1;
});
dump_points(@points);

sub parse_ucd_file {
	my $ucd_file = shift;
	my $callback = shift;

	open UCD_FILE, $ucd_file or die "Could not open $ucd_file: $!";

	while (my $line = <UCD_FILE>) {
		chomp $line;

		# Strip comments
		$line =~ s/#.*//;

		# Skip blank lines
		next unless $line =~ /\S/;

		$callback->(split /\s*;\s*/, $line) or last;
	}

	close UCD_FILE;

	return 1;
}

sub dump_blocks {
	print <<JAVA;
// BEGIN GENERATED CODE: Blocks.txt, cutoff=@{[CUTOFF_STRING]}
private static List<Block> blocks = Arrays.asList(new Block[] {
JAVA

	for my $block (@_) {
		my ($first, $last, $name) = @$block;

	print <<JAVA;
	new Block("$name", 0x$first, 0x$last),
JAVA

	}

	print <<JAVA;
});
// END GENERATED CODE
JAVA
}

sub dump_points {
	my $size = @_;
	my $storage = 1 + hex $_[-1][0];

	print <<JAVA;
// BEGIN GENERATED CODE: UnicodeData.txt, cutoff=@{[CUTOFF_STRING]}
private static final int actualSize = $size;
private static final String[] characterNames = new String[$storage];
JAVA

	my $method_index = -1;
	while (@_) {
		my @entries = splice @_, 0, 2048;

		$method_index++;

		print <<JAVA;

private static void loadCharacterNames$method_index()
{
JAVA
		for my $entry (@entries) {
			my ($point, $name) = @$entry;

			print <<JAVA;
	characterNames[0x$point] = "$name";
JAVA
		}

		print <<JAVA;
}
JAVA
	}

	print <<JAVA;

static
{
JAVA
	for (my $i = 0; $i <= $method_index; $i++) {
		print <<JAVA;
	loadCharacterNames$i();
JAVA
	}

	print <<JAVA;
}
// END GENERATED CODE
JAVA
}

