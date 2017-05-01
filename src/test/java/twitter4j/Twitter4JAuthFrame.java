package twitter4j;

import java.awt.Container;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jjYBdx4IL.utils.awt.AWTUtils;

import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * just for testing, needs reworking
 * 
 * @author jjYBdx4IL
 *
 */
public class Twitter4JAuthFrame extends JFrame implements ActionListener {

    private static final Logger LOG = LoggerFactory.getLogger(Twitter4JAuthFrame.class);

    private JLabel label = new JLabel("Go to the following URL and enter the PIN below");
    private JButton urlButton = new JButton();
    private JTextField pinTF = new JTextField("");
    private JButton saveButton = new JButton("save");

    private Twitter twitter = null;
    private RequestToken requestToken = null;

    public Twitter4JAuthFrame() {
        super("Twitter Authentication");
        
        updateRequestToken();

        urlButton.addActionListener(this);
        saveButton.addActionListener(this);

        Container container = getContentPane();
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;

        container.add(label, c);

        c.gridy++;

        container.add(urlButton, c);

        c.gridy++;

        container.add(pinTF, c);

        c.gridy++;

        container.add(saveButton, c);
    }
    
    private void updateRequestToken() {
        try {
            twitter = Twitter4JTestConfig.getTwitter();
            requestToken = twitter.getOAuthRequestToken("oob");
            String authURL = requestToken.getAuthorizationURL();
            LOG.info("new auth URL: " + authURL);
            urlButton.setText(authURL);
        } catch (TwitterException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOG.info(e.toString());
        if (e.getSource() == saveButton) {
            try {
                AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, pinTF.getText());
                storeAccessToken(twitter.verifyCredentials().getId(), accessToken);
                setVisible(false);
            } catch (TwitterException | IOException e1) {
                LOG.error("credentials invalid, please retry", e1);
                updateRequestToken();
                AWTUtils.showInfoDialogOnMouseScreen("Failure", "Authorization link updated, please re-open your browser!");
            }
        }
        if (e.getSource() == urlButton) {
            try {
                Desktop.getDesktop().browse(new URI(urlButton.getText()));
            } catch (IOException | URISyntaxException e1) {
                LOG.error("", e1);
            }
        }
    }

    private static void storeAccessToken(long useId, AccessToken accessToken) throws FileNotFoundException, IOException {
        Twitter4JTestConfig config = new Twitter4JTestConfig();
        config.token = accessToken.getToken();
        config.tokenSecret = accessToken.getTokenSecret();
        config.write();
    }

}

