package nested ; 

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.syntax.DefaultTokenHandler;
import org.gjt.sp.jedit.syntax.Token;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.jedit.textarea.TextAreaExtension;
import org.gjt.sp.util.Log;

public class NestedTextAreaExtension extends TextAreaExtension {

	private TextArea textArea ; 

	public NestedTextAreaExtension( TextArea textArea ){
		this.textArea=textArea ;
	}

	@Override                       
	public void paintValidLine(Graphics2D gfx, int screenLine,
			int physicalLine, int start, int end, int y){

		JEditBuffer buffer = textArea.getBuffer( ) ;
		String mode = buffer.getMode().toString() ;
		Token token = getToken( physicalLine, buffer ) ;
		int height = textArea.getPainter().getFontMetrics().getHeight() ; 
		Color before = gfx.getColor( ) ;
		
		int offset = 0 ;
		
		if( token.id == Token.END){
			// this means that this line has only one token
			String tokenmode = token.rules.getModeName() ;
			if( !mode.equals(tokenmode) ){
				Point p1 = new Point( 0, 0 ) ;
				Point p2 = new Point( textArea.getWidth(), 0 ) ;
				gfx.setColor( nested.Plugin.getModel().getColor( mode, tokenmode ) ) ;
				gfx.fillRect( p1.x , y, p2.x - p1.x, height ) ;
				gfx.setColor( before ) ;
			}
			return ;
		}
		while( token.id != Token.END ){
			int length = token.length ;      
			if( !token.rules.getModeName( ).equals( mode ) ){
				String tokenmode = token.rules.getModeName( ) ;

				Point p1; 
				Point p2;

				if( offset == 0 ){
					p1 = new Point( 0, 0 ) ; 
				} else{
					p1 = textArea.offsetToXY( start + offset ) ;
				}

				if( token.next.id == Token.END ){
					p2 = new Point( textArea.getWidth(), 0 )  ;
				} else{
					p2 = textArea.offsetToXY( start + offset + length ) ;
				}

				if( p1 != null && p2 != null ){
					gfx.setColor( nested.Plugin.getModel().getColor( mode, tokenmode ) ) ;
					gfx.fillRect( p1.x , y, p2.x - p1.x, height ) ;
					gfx.setColor( before ) ;
				}

			}
			offset += length ;
			token = token.next ;
		}
	}          

	private Token getToken( int line, JEditBuffer buffer ){
		DefaultTokenHandler list = new DefaultTokenHandler() ;
		buffer.markTokens(line,list) ;
		return list.getTokens() ;
	}
	

}
