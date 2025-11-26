import onnxruntime as rt
import numpy as np
import pandas as pd

def test_model(model_path):
    print(f"Loading model from {model_path}...")
    sess = rt.InferenceSession(model_path)

    input_name = sess.get_inputs()[0].name
    label_name = sess.get_outputs()[0].name
    
    print("Model Inputs:")
    for i in sess.get_inputs():
        print(f" - {i.name}: {i.type} {i.shape}")

    # Create a sample input

    # Sample data: 
    # stream='Bio Science', district='Colombo', difficulty_level=6, current_score=70, 
    # english_fluency=8, tuition_hours_weekly=5, commute_fatigue=0.2
    inputs = {
        'stream': np.array([['Bio Science']],  dtype=object),
        'district': np.array([['Colombo']], dtype=object),
        'difficulty_level': np.array([[6]], dtype=np.int64),
        'current_score': np.array([[70.0]], dtype=np.float64),
        'english_fluency': np.array([[8]], dtype=np.int64),
        'tuition_hours_weekly': np.array([[5]], dtype=np.int64),
        'commute_fatigue': np.array([[0.2]], dtype=np.float64)
    }
 
    print("\nPredicting for sample student:")
    print(inputs)
    
    try:
        feed_dict = {}
        for i in sess.get_inputs():
            if i.name in inputs:
                # Cast to correct type if necessary
                # ONNX Runtime is strict about types
                target_type = i.type
                data = inputs[i.name]
                
                if 'string' in target_type:
                    feed_dict[i.name] = data.astype(object) # or str
                elif 'float' in target_type:
                    feed_dict[i.name] = data.astype(np.float32)
                elif 'int' in target_type:
                    feed_dict[i.name] = data.astype(np.int64)
                else:
                    feed_dict[i.name] = data
            else:
                print(f"Warning: Input {i.name} not found in sample data")

        pred_onx = sess.run([label_name], feed_dict)[0]
        print(f"\nPredicted Hours Needed: {pred_onx[0][0]:.2f}")
        
    except Exception as e:
        print(f"\nError during prediction: {e}")
        print("Please check input types and names.")

if __name__ == "__main__":
    test_model("Study_predictor_improved.onnx")
