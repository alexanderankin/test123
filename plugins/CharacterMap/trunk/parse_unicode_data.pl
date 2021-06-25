#!/usr/bin/perl -w

use strict;

# BMP Plane Unicode chars
# use constant CUTOFF => 0xFFFF;
# use constant CUTOFF_STRING => sprintf '0x%04X', CUTOFF;

# All Unicode chars
use constant CUTOFF => 0x10FFFF;
use constant CUTOFF_STRING => sprintf '0x%06X', CUTOFF;

use subs qw( parse_ucd_file dump_blocks );

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
private static final List<UnicodeData.Block> blocks = Arrays.asList(new UnicodeData.Block[] {
JAVA

	for my $block (@_) {
		my ($first, $last, $name) = @$block;

	print <<JAVA;
	new UnicodeData.Block("$name", 0x$first, 0x$last),
JAVA

	}

	print <<JAVA;
});
// END GENERATED CODE
JAVA
}


