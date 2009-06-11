#
# script to report on which schemes are missing which properties
#
# TODO: find 'extra' properties in schemes

def getPropertyNames():
	from editorscheme import EditorScheme
	names = []
	for group in EditorScheme.getPropertyGroups():
		for name in group.getPropertyNames():
			names.append(name)
	return names


def getSchemes():
	from editorscheme import EditorSchemePlugin
	EditorSchemePlugin.loadSchemes()
	return EditorSchemePlugin.getSchemes()


if __name__ in ('main','__main__'):
	import sys,os

	print 'looking for EditorScheme.jar...',
	if os.path.exists(jEdit.getSettingsDirectory() + os.sep + 'jars' + os.sep + 'EditorScheme.jar'):
		print 'OK (in user plugins directory)'
		sys.path.append(jEdit.getSettingsDirectory() + os.sep + 'jars' + os.sep + 'EditorScheme.jar')
	elif os.path.exists(jEdit.getJEditHome() + os.sep + 'jars' + os.sep + 'EditorScheme.jar'):
		print 'OK (inglobal plugins directory)'
		sys.path.append(jEdit.getJEditHome() + os.sep + 'jars' + os.sep + 'EditorScheme.jar')
	else:
		print 'FAILED, NOT FOUND.'

	# get all schemes from plugin
	schemes = getSchemes()
	# get all expected property names
	props = getPropertyNames()

	for scheme in schemes:
		complete = 1
		print scheme.getName()
		for name in props:
			if scheme.properties.get(name) == None:
				print '\t' + name
				complete = 0
		if complete:
			print '\tNone'
	print 'done'


