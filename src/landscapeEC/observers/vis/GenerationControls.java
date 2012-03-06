package landscapeEC.observers.vis;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import landscapeEC.core.GARun;
import landscapeEC.observers.Observer;

public class GenerationControls extends JFrame implements Observer {
    private static final long serialVersionUID = 4625378208888714559L;
    private int width = 300;
    private int height = 70;
    private PlaybackState currentState = PlaybackState.PAUSE;
    private PlaybackState prevState = PlaybackState.PAUSE;
    
    public GenerationControls() {
        setupFrame();
    }

    private void setupFrame() {
        setSize(width, height);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(this.getLocation().x + width, this.getLocation().y+height);
        
        JPanel panel = new JPanel();
        
        JButton pauseButton = new JButton(" Run ");
        JButton playButton = new JButton("Pause");
        JButton stepButton = new JButton("Step");
        
        pauseButton.addActionListener(new PlaybackControlListener());
        playButton.addActionListener(new PlaybackControlListener());
        stepButton.addActionListener(new PlaybackControlListener());
        
        panel.add(playButton);
        panel.add(pauseButton);
        panel.add(stepButton);
        
        this.getContentPane().add(panel);
        pack();
    }
    
    @Override
    public void generationData(GARun run) {
        switch(currentState) {
        case PAUSE:
            run.allowGenerationRunning(false);
            break;
        case PLAY:
            run.allowGenerationRunning(true);
            break;
        case STEP:
            if (prevState == PlaybackState.PLAY) {
                run.allowGenerationRunning(false);
                currentState = PlaybackState.PAUSE;
            } else {
                run.allowGenerationRunning(true);
                currentState = PlaybackState.PAUSE;
            }
            break;
        }
        prevState = currentState;
    }
    
    private class PlaybackControlListener implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent event) {
            JButton source = ((JButton) event.getSource());
            
            if(source.getText().equals(" Run ")) {
                currentState = PlaybackState.PLAY;
            } else if(source.getText().equals("Pause")) {
                currentState = PlaybackState.PAUSE;
            } else {
                currentState = PlaybackState.STEP;
            }
        }
        
    }
    
    private enum PlaybackState { PLAY, PAUSE, STEP}
}
