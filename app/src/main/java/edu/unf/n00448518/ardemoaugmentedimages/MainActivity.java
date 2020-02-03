package edu.unf.n00448518.ardemoaugmentedimages;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements Scene.OnUpdateListener
{

    private CustomARFragment arFragment;
    final Handler handler = new Handler();
    private Button startButton;
    private Button stopButton;
    private Spinner choiceSpinner;
    private Timer timer = new Timer();
    protected String model = "Plain_Chord.sfb";
    protected int ctr = 0;
    class SongTask extends TimerTask
    {
        @Override
        public void run()
        {
            handler.post(new Runnable ()
            {
                public void run()
                {
                    switch (ctr)
                    {
                        case 0:
                            //G
                            model = "G_chord.sfb";//first string for object
                            break;
                        case 1:
                            //D
                            model = "D_chord.sfb";//second string for object
                            break;
                        case 2:
                            //Em
                            model = "Em.sfb";//third string for object
                            break;
                        case 3:
                            //C
                            model = "C_chord (1).sfb";//fourth string for object
                            break;
                        case 4:
                            //G
                            model = "G_chord.sfb";//first string for object
                            break;
                        case 5:
                            //D
                            model = "D_chord.sfb";//second string for object
                            break;
                        case 6:
                        case 7:
                            //C
                            model = "C_chord (1).sfb";//fourth string for object
                            break;
                    }
                    if (++ctr > 7)
                        ctr = 0;
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arFragment = (CustomARFragment) getSupportFragmentManager().findFragmentById(R.id.arfragment);
        arFragment.getArSceneView().getScene().addOnUpdateListener(this);

        choiceSpinner = findViewById(R.id.choiceSpinner);
        ArrayAdapter<CharSequence> arrAdapter = ArrayAdapter.createFromResource(this,
                R.array.choices_array, android.R.layout.simple_spinner_item);
        arrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        choiceSpinner.setAdapter(arrAdapter);

        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String choice = choiceSpinner.getSelectedItem().toString();
                switch (choice){
                    case "G":
                        model = "G_chord.sfb";
                        break;
                    case "Em":
                        model = "Em.sfb";
                        break;
                    case "D":
                        model = "D_chord.sfb";
                        break;
                    case "C":
                        model = "C_chord (1).sfb";
                        break;
                    case "Wagon Wheel":
                        ctr = 0;
                        timer.scheduleAtFixedRate(new SongTask(), 0, 2000);
                        break;
                }
            }
        });

        stopButton = findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                timer.cancel();
                model = "Plain_Chord.sfb";
                timer = new Timer();
            }
        });
    }

    public void setupDatabase(Config config, Session session)
    {
        Bitmap targetImageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.targetimage);
        AugmentedImageDatabase aid = new AugmentedImageDatabase(session);
        aid.addImage("targetimage", targetImageBitmap);
        config.setAugmentedImageDatabase(aid);
    }

    @Override
    public void onUpdate(FrameTime frameTime)
    {
        Frame frame = arFragment.getArSceneView().getArFrame();
        Collection<AugmentedImage> images = frame.getUpdatedTrackables(AugmentedImage.class);

        for (AugmentedImage image: images)
        {
            if (image.getTrackingState() == TrackingState.TRACKING)
            {
                if (image.getName().equals("targetimage"))
                {
                    Anchor anchor = image.createAnchor(image.getCenterPose());

                    createModel(anchor, model);
                }
            }
        }
    }

    private void createModel(Anchor anchor, String model)
    {
        ModelRenderable.builder()
                .setSource(this, Uri.parse(model))
                .build()
                .thenAccept(modelRenderable -> placeModel(modelRenderable, anchor));
    }

    private void placeModel(ModelRenderable modelRenderable, Anchor anchor)
    {
        Scene scene = arFragment.getArSceneView().getScene();
        List<Node> anchors = scene.getChildren();
        scene.removeChild(anchors.get(anchors.size() - 1));
        AnchorNode anchornode = new AnchorNode(anchor);
        anchornode.setRenderable(modelRenderable);
        scene.addChild(anchornode);

    }
}
