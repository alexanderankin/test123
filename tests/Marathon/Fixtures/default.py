from org.gjt.sp.jedit import jEdit

class Fixture:
    def start_application(self):
        args = ["-reuseview"]
        jEdit.main(args)

    def teardown(self):
        pass

    def setup(self):
        self.start_application()

