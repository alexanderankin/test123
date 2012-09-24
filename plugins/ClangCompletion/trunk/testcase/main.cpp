
#include <QtGui/QApplication>
#include <QtGui/QMessageBox>

int main (int argc, char* argv[]) {
    QApplication app(argc, argv);

    QMessageBox::information(0, "My Application", 
        "This is some text that goes in the message box.",
        QMessageBox::Ok, QMessageBox::Ok);
    // app.   // try completion on app.
    return 0;
}