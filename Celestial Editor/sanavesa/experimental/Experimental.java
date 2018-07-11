package sanavesa.experimental;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Experimental extends Application
{
	public static void main(String[] args)
	{
		launch(args);
	}
	

    @Override
    public void start(Stage stage) throws Exception {

        ListView<String> birdList = new ListView<>();
        birdList.getItems().addAll("A", "B", "C", "D");
        birdList.setCellFactory(param -> new BirdCell());
        birdList.setPrefWidth(90);
        birdList.setPrefHeight(180);

        VBox layout = new VBox(birdList);
        layout.setPadding(new Insets(10));

        stage.setScene(new Scene(layout));
        stage.show();
    }

    private class BirdCell extends ListCell<String> {
        private final ImageView imageView = new ImageView();

        public BirdCell() {
            ListCell thisCell = this;

            setContentDisplay(ContentDisplay.CENTER);
            setAlignment(Pos.CENTER);

            setOnDragDetected(event -> {
                if (getItem() == null) {
                    return;
                }

                ObservableList<String> items = getListView().getItems();

                Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(getItem());
                dragboard.setDragView(new Text(getItem()).snapshot(null,  null));
                dragboard.setContent(content);

                event.consume();
            });

            setOnDragOver(event -> {
            	
            	event.consume();
            	
            	if (event.getGestureSource() != thisCell &&
                        event.getDragboard().hasString()) {
            		event.acceptTransferModes(TransferMode.MOVE);
            		
                    setOpacity(0.5);
	            	Node node = (Node) event.getSource();
	                Point2D mouse = node.localToParent(event.getX(), event.getY());
	                Bounds bounds = getBoundsInParent();
	                
	                // If over 75%, put below, so highlight above
	                if(mouse.getY() >= bounds.getMinY() + bounds.getHeight()*0.75)
	                {
	                	setStyle("-fx-border-color: blue;\n"
	                    		+ "-fx-border-width: 0 0 3 0;\n"
	                    		+ "-fx-border-style: dashed;\n");
	                }
	                // If below 25%, put above, so highlight below
	                else if(mouse.getY() <= bounds.getMinY() + bounds.getHeight()*0.25)
	                {
	                	setStyle("-fx-border-color: blue;\n"
	                    		+ "-fx-border-width: 3 0 0 0;\n"
	                    		+ "-fx-border-style: dashed;\n");
	                }
	                // Else, swap
	                else
	                {
	                	setStyle("-fx-border-color: blue;\n"
	                    		+ "-fx-border-width: 3 3 3 3;\n"
	                    		+ "-fx-border-style: dashed;\n");
	                }
            	}
            });
            
            setOnDragEntered(event -> {
            	
            	if (event.getGestureSource() != thisCell &&
                        event.getDragboard().hasString()) {
                    setOpacity(0.5);
	            	Node node = (Node) event.getSource();
	                Point2D mouse = node.localToParent(event.getX(), event.getY());
	                Bounds bounds = getBoundsInParent();
	                
	                // If over 75%, put below, so highlight above
	                if(mouse.getY() >= bounds.getMinY() + bounds.getHeight()*0.75)
	                {
	                	setStyle("-fx-border-color: blue;\n"
	                    		+ "-fx-border-width: 0 0 3 0;\n"
	                    		+ "-fx-border-style: dashed;\n");
	                }
	                // If below 25%, put above, so highlight below
	                else if(mouse.getY() <= bounds.getMinY() + bounds.getHeight()*0.25)
	                {
	                	setStyle("-fx-border-color: blue;\n"
	                    		+ "-fx-border-width: 3 0 0 0;\n"
	                    		+ "-fx-border-style: dashed;\n");
	                }
	                // Else, swap
	                else
	                {
	                	setStyle("-fx-border-color: blue;\n"
	                    		+ "-fx-border-width: 3 3 3 3;\n"
	                    		+ "-fx-border-style: dashed;\n");
	                }
            	}
            });

            setOnDragExited(event -> {
                if (event.getGestureSource() != thisCell &&
                        event.getDragboard().hasString()) {
                    setOpacity(1);
                    setStyle("-fx-border-width: 0;");
                }
            });

            setOnDragDropped(event -> {
                if (getItem() == null) {
                    return;
                }
                
                Dragboard db = event.getDragboard();
                boolean success = false;

                if (db.hasString()) {
                    ObservableList<String> items = getListView().getItems();
                    int draggedIndex = items.indexOf(db.getString());
                    int thisIndex = items.indexOf(getItem());

                    Node node = (Node) event.getSource();
                    Point2D mouse = node.localToParent(event.getX(), event.getY());
                    Bounds bounds = getBoundsInParent();
                    
                    // If over 75%, put below
                    if(mouse.getY() >= bounds.getMinY() + bounds.getHeight()*0.7)
                    {
                    	System.out.println("putting " + db.getString() + " below " + getItem());
                    	int insertionIndex = -1;
                    	
                    	if(draggedIndex >= thisIndex)
                    		insertionIndex = Math.min(thisIndex + 1, items.size());
                    	else
                    		insertionIndex = thisIndex;
                    	
                    	items.remove(draggedIndex);
                        items.add(insertionIndex, db.getString());
                    }
                    // If below 25%, put above
                    else if(mouse.getY() <= bounds.getMinY() + bounds.getHeight()*0.3)
                    {
                    	System.out.println("putting " + db.getString() + " above " + getItem());
                    	int insertionIndex = -1;
                    	
                    	if(draggedIndex >= thisIndex)
                    		insertionIndex = thisIndex;
                    	else
                    		insertionIndex = Math.max(thisIndex -1, 0);
                    	
                    	items.remove(draggedIndex);
                        items.add(insertionIndex, db.getString());
                    }
                    // Else, swap
                    else
                    {
                    	System.out.println("swapping " + db.getString() + " with " + getItem());
                    	
                    	items.set(draggedIndex, getItem());
                    	items.set(thisIndex, db.getString());
                    }
                    
                    getListView().refresh();

                    success = true;
                }
                event.setDropCompleted(success);

                event.consume();
            });

            setOnDragDone(DragEvent::consume);
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setGraphic(null);
            } else {
            	setText(item);
//                imageView.setImage(
//                    birdImages.get(
//                        getListView().getItems().indexOf(item)
//                    )
//                );
//                setGraphic(imageView);
            }
        }
    }
	
//	@Override
//	public void start(Stage primaryStage) throws Exception
//	{
//		Group root = new Group();
//		
//		Rectangle rect1 = new Rectangle(64, 64, Color.RED);
//		rect1.relocate(50, 240);
//		
//		Rectangle rect2 = new Rectangle(64, 64, Color.BLACK);
//		rect2.relocate(150, 240);
//		
//		Rectangle rect3 = new Rectangle(64, 64, Color.BLUE);
//		rect3.relocate(250, 240);
//		
//		MouseGestures mg = new MouseGestures();
//		mg.makeDraggable(rect1);
//		mg.makeDraggable(rect2);
//		mg.makeDraggable(rect3);
//		
//		root.getChildren().addAll(rect1, rect2, rect3);
//		
//		primaryStage.setScene(new Scene(root, 640, 480));
//		primaryStage.show();
//	}
//	
//	static class MouseGestures
//	{
//		class DragContext
//		{
//			double x, y;
//		}
//		
//		DragContext dragContext = new DragContext();
//		
//		public void makeDraggable(Node node)
//		{
//			node.setOnMousePressed(onMousePressedEventHandler);
//			node.setOnMouseDragged(onMouseDraggedEventHandler);
//		}
//		
//		EventHandler<MouseEvent> onMousePressedEventHandler = event ->
//		{
//			Node node = ((Node) (event.getSource()));
//			
//			dragContext.x = node.getTranslateX() - event.getSceneX();
//			dragContext.y = node.getTranslateY() - event.getSceneY();
//		};
//		
//		EventHandler<MouseEvent> onMouseDraggedEventHandler = event ->
//		{
//			Node node = ((Node) (event.getSource()));
//			
//			node.setTranslateX(dragContext.x + event.getSceneX());
//			node.setTranslateY(dragContext.y + event.getSceneY());
//		};
//	}
}












