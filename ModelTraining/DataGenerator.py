import pandas as pd
import numpy as np

# setup
NUM_STUDENTS  = 10000
np.random.seed(42)

#Streams contex
streams = ['Physical Science', 'Bio Science', 'Commerce', 'Arts', 'Technology', 'English', 'Sinhala']
stream_weights = [0.30, 0.20, 0.20, 0.10, 0.10, 0.07, 0.03]

# Location aspects 
districts = ['Ratnapura','Colombo', 'Gampaha',  'Kandy', 'Jaffna','Galle', 'UpCountry', 'RemoteArea' ]
district_weight = [0.10,0.2,0.2,0.15,0.1,0.15,0.06,0.04]

#Generate Base Data
data = pd.DataFrame({
    'student_id': range(1,NUM_STUDENTS+1),
    'stream': np.random.choice(streams, NUM_STUDENTS, p=stream_weights),
    'district':np.random.choice(districts,NUM_STUDENTS, p=district_weight), 
    'difficulty_level':np.random.randint(1,11,NUM_STUDENTS),
})

def get_englishfleency(district):
    if district in ['Colombo', 'Kandy']:
        return np.random.randint(6,11)
    elif district in ['Gampaha', 'Galle', 'Ratnapura','Jaffna' ]:
        return np.random.randint(4,9)
    else:
        return np.random.randint(2,7)
    
data ['english_fluency'] = data['district'].apply(get_englishfleency)
# Normally Sri lanka have lots tution
data['tuition_hours_weekly'] = np.random.randint(0,16,NUM_STUDENTS)
#infastructure 
data['commute_fatigue'] = np.random.uniform(0.0,0.5,NUM_STUDENTS)

#how effected by tution 
base_score = np.random.normal(60,15,NUM_STUDENTS)
data['current_score'] = (base_score+ (data['tuition_hours_weekly']*0.5)).clip(0,100)

def calculate_sl_hours (row):
    hours = row['difficulty_level'] * 2.0

    #Science Screem normally need more grind
    if row['stream'] in ['Physical Science', 'Bio Science']:
        hours *= 1.2
    # English barrier
    if row ['english_fluency'] < 5:
        hours += 1.5
    #Smart Student
    hours -= (row['current_score'] /30)

    # If some one go toomush tution

    if row ['tuition_hours_weekly'] >10:
        hours *= 1.1
    
    hours += (row['commute_fatigue']*2)

    noise = np.random.normal(0,0.5)
    return max(1.0, round(hours + noise, 1))

data ['hours_needed'] = data.apply(calculate_sl_hours, axis=1)

# 6. Save
data.to_csv("sri_lankan_student_data.csv", index=False)
print(data[['stream', 'district', 'english_fluency', 'tuition_hours_weekly', 'hours_needed']].head())