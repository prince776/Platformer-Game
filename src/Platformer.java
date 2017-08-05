import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.Random;

import javax.swing.JFrame;


public class Platformer implements Runnable{
	
	private JFrame frame;
	private Canvas canvas;
	private Thread thread;
	
	private Key key;
	
	private BufferStrategy bs;
	private Graphics g;
	
	int px =100,py=1,mx=0,my=0,G=1;
	
	byte count=1;
	
	Random rand;
	
	block[] blocks;
	
	public Platformer(){
		rand = new Random();
		blocks = new block[rand.nextInt(22)+10];
		
		frame = new JFrame("Platformer");
		frame.setSize(500,500);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		key = new Key();
		frame.setLocationRelativeTo(null);
		frame.addKeyListener(key);
		
		canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(500,500));
		canvas.setMaximumSize(new Dimension(500,500));
		canvas.setMinimumSize(new Dimension(500,500));
		canvas.setFocusable(false);
		frame.add(canvas);
		frame.pack();
		
		for(int i=0;i<blocks.length;i++){
			blocks[i] = new block();
			blocks[i].x = rand.nextInt(500);
			blocks[i].y = rand.nextInt(380)+100;
			blocks[i].width = rand.nextInt(40)+45;
			blocks[i].height = rand.nextInt(15)+20;
			blocks[i].bounds = new Rectangle(blocks[i].x,blocks[i].y,blocks[i].width,blocks[i].height);

		}
		
	}
	
	public synchronized void start(){
		thread = new Thread(this);
		thread.start();
	}
	
	public void run(){
		
		long now,lastTime=System.nanoTime();
		double delta=0,nsPerTick = 1000000000/60;
		
		while(true){
			now = System.nanoTime();
			delta+=(now-lastTime)/nsPerTick;
			lastTime=now;
			
			if(delta>=1){
				tick();
				render();
				delta--;
				count++;

			}
			
		}
		
	}
	
	public void tick(){
	
		key.tick();

		if(key.up){
			my=-15;
		}
		if(key.down)
			my=2;
		if(key.left)
			mx=-2;
		if(key.right)
			mx=2;
		for(int i=0;i<blocks.length;i++){
			blocks[i].tick();
		}		
		my+=G+count;

		px+=mx;
		py+=my;
		
		if(px<=0)
			px=0;
		if(px>=500-10)
			px = 480;
		if(py<=0)
			py=0;
		if(py>=480){
			py=480;
			count=1;
		}
		mx=0;
		my=0;
		
	}
	
	public void render(){
		
		
		
		bs = canvas.getBufferStrategy();
		
		if(bs==null){
			canvas.createBufferStrategy(3);
			return;
		}
		
		g = bs.getDrawGraphics();
		g.clearRect(0, 0, 500, 500);

		
		g.setColor(Color.black);
		g.fillRect(0, 0, 500, 500);
		for(int i=0;i<blocks.length;i++){
			blocks[i].render();
		}
		
		g.setColor(Color.red);
		g.fillRect(px, py, 10, 20);
		
		bs.show();g.dispose();
	}
	
	public synchronized void stop(){
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		Platformer g = new Platformer();
		g.start();
	}
	
	class Key implements KeyListener{
		boolean[] keys = new boolean[256];
		
		boolean up,down,left,right;
		
		public void tick(){
			up= keys[KeyEvent.VK_UP] ;
			down=keys[KeyEvent.VK_DOWN] ;
			left=keys[KeyEvent.VK_LEFT] ;
			right= keys[KeyEvent.VK_RIGHT] ;

		}
		@Override
		public void keyPressed(KeyEvent e) {
			keys[e.getKeyCode()] = true;
			
		}

		@Override
		public void keyReleased(KeyEvent e) {
			keys[e.getKeyCode()] = false;
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	class block {
		int x,y,width,height;
		Rectangle bounds = new Rectangle(x,y,width,height);
		public void tick(){
			if(getCollisionBounds(0, 0).intersects(new Rectangle(px+mx,py,10,20))){
				mx=0;
				System.out.println("YES");
			}
			if( getCollisionBounds(0, 0).intersects(new Rectangle(px,py+my+G+count,10,20))){
					count=0;
					G=0;
				
				my=0;

				System.out.println("YES");
			}
		}
		public Rectangle getCollisionBounds(float xOffset,float yOffset){
			return new Rectangle( (int)(x + xOffset),(int)(y+yOffset),width,height  );
		}
		public void render(){
			g.setColor(Color.white);
			g.fillRect(x, y, width, height);
		}
		
	}
	
}

//in just 200 lines..if import lines and blank line not counted